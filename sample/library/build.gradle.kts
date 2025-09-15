@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidLibrary)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.library"
  compileSdk = 36

  defaultConfig {
    minSdk = 24
  }
}
