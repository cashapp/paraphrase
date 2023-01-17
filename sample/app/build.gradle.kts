@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidApplication)
  id("app.cash.gingham")
}

android {
  namespace = "app.cash.gingham.sample.app"
  compileSdk = 33

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.3.1"
  }

  defaultConfig {
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

dependencies {
  implementation(projects.sample.library)
  implementation(libs.androidActivityCompose)
  implementation(libs.googleMaterial)
  implementation(libs.composeMaterial)
  implementation(libs.composeUi)

  coreLibraryDesugaring(libs.coreLibraryDesugaring)
}
