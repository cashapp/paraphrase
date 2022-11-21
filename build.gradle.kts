plugins {
  id("com.android.application") version "8.0.0-alpha02" apply false
  id("com.android.library") version "8.0.0-alpha02" apply false
  id("com.diffplug.spotless") version "6.11.0"
  id("org.jetbrains.kotlin.android") version "1.7.10" apply false
  id("org.jetbrains.kotlin.jvm") version "1.7.10" apply false
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
