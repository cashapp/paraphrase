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

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}
