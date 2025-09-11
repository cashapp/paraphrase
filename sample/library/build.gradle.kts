@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.library"
  compileSdk = 36

  defaultConfig {
    minSdk = 24
  }
}

androidComponents {
  beforeVariants {
    if (it.buildType == "release") {
      it.enable = false
    }
  }
}
