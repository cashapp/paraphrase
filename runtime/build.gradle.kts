@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
  alias(libs.plugins.poko)
}

android {
  namespace = "app.cash.paraphrase"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
  }
}

dependencies {
  api(libs.androidAnnotation)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
}
