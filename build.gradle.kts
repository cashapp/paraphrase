import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

buildscript {
  dependencies {
    classpath("app.cash.paraphrase:plugin")
  }
}

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.androidTest) apply false
  alias(libs.plugins.kotlinAndroid) apply false
  alias(libs.plugins.kotlinJvm) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.spotless)
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

  plugins.withId("org.jetbrains.kotlin.android") {
    extensions.getByType<KotlinTopLevelExtension>().jvmToolchain {
      languageVersion.set(JavaLanguageVersion.of(8))
    }
  }
}
