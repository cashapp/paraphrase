@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.mavenPublish)
}

android {
  namespace = "app.cash.gingham"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }
}

dependencies {
  api(libs.androidAnnotation)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
}
