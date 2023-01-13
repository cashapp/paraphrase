@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  kotlin("android")
  id("app.cash.gingham")
  alias(libs.plugins.androidLibrary)
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
