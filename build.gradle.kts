import com.diffplug.gradle.spotless.SpotlessExtension
import com.vanniktech.maven.publish.MavenPublishPlugin

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

  plugins.withType(MavenPublishPlugin::class) {
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
        maven {
          name = "internal"
          setUrl(providers.gradleProperty("internalUrl"))
          credentials(PasswordCredentials::class)
        }
      }
    }
  }
}
