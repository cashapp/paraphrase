import com.android.build.api.dsl.CommonExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

buildscript {
  dependencies {
    classpath("app.cash.paraphrase:plugin")
  }
}

plugins {
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.androidTest) apply false
  alias(libs.plugins.kotlinJvm) apply false
  alias(libs.plugins.kotlinParcelize) apply false
  alias(libs.plugins.poko) apply false
  alias(libs.plugins.kotlinApiDump) apply false
  alias(libs.plugins.dokka)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.spotless)
}

dependencies {
  dokka(projects.runtime)
  dokka(projects.runtimeComposeUi)
}

configure<SpotlessExtension> {
  kotlin {
    target("**/*.kt")
    ktlint(libs.versions.ktlint.get())
    licenseHeaderFile(file("gradle/license-header.txt"))
  }
  kotlinGradle {
    ktlint(libs.versions.ktlint.get())
  }
}

subprojects {
  version = extra["VERSION_NAME"]!!

  plugins.withId("com.vanniktech.maven.publish") {
    // Disable Javadoc jars. They're basically useless relics, but enabling this will also cause
    // AGP to use an old version of Dokka which fails to run on the latest Java versions.
    extensions.getByType(MavenPublishBaseExtension::class)
      .configureBasedOnAppliedPlugins(javadocJar = false)

    // All published libraries must use API tracking to help maintain compatibility.
    plugins.apply(libs.plugins.kotlinApiDump.get().pluginId)

    val kotlin = extensions.getByName("kotlin") as KotlinBaseExtension
    kotlin.explicitApi()

    publishing {
      repositories {
        /**
         * Want to push to an internal repository for testing?
         * Set the following properties in ~/.gradle/gradle.properties.
         *
         * internalUrl=YOUR_INTERNAL_URL
         * internalUsername=YOUR_USERNAME
         * internalPassword=YOUR_PASSWORD
         */
        val internalUrl = providers.gradleProperty("internalUrl")
        if (internalUrl.isPresent()) {
          maven {
            name = "internal"
            setUrl(internalUrl)
            credentials(PasswordCredentials::class)
          }
        }
      }
    }
  }

  val javaVersion = JavaVersion.VERSION_1_8
  tasks.withType<KotlinJvmCompile> {
    compilerOptions {
      jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
    }
  }
  val configureAndroid = Action<Plugin<Any>> {
    with(extensions.getByType<CommonExtension>()) {
      compileSdk = 36
      defaultConfig.minSdk = 24

      compileOptions.apply {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
      }
    }
  }
  plugins.withId("com.android.application", configureAndroid)
  plugins.withId("com.android.library", configureAndroid)
  plugins.withId("com.android.test", configureAndroid)
}
