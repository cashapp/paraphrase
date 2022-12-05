@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
  kotlin("android") version libs.versions.kotlin apply false
  kotlin("jvm") version libs.versions.kotlin apply false
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.spotless)
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    target("**/*.kt")
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
