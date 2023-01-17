@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidLibrary)
  id("app.cash.gingham")
}

android {
  namespace = "app.cash.gingham.sample.library"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }
}
