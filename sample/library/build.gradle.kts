@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidLibrary)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.library"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }
}

androidComponents {
  beforeVariants {
    if (it.buildType == "release") {
      it.enable = false
    }
  }
}
