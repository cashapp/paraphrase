import com.diffplug.gradle.spotless.SpotlessExtension
import com.vanniktech.maven.publish.MavenPublishPlugin

buildscript {
  dependencies {
    classpath("app.cash.gingham:plugin")
  }
}

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.kotlinAndroid) apply false
  alias(libs.plugins.kotlinJvm) apply false
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.spotless)
}

configure<SpotlessExtension> {
  kotlin {
    target("src/**/*.kt")
    licenseHeader("// Copyright Square, Inc.")
    // ktlint doesn't honour .editorconfig yet: https://github.com/diffplug/spotless/issues/142
    ktlint("0.41.0").userData(
      mapOf(
        "insert_final_newline" to "true",
        "end_of_line" to "lf",
        "charset" to "utf-8",
        "indent_size" to "2",
      )
    )
  }
  kotlinGradle {
    ktlint("0.41.0").userData(
      mapOf(
        "insert_final_newline" to "true",
        "end_of_line" to "lf",
        "charset" to "utf-8",
        "indent_size" to "2",
      )
    )
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
