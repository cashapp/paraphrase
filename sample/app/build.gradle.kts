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
    applicationId = "app.cash.gingham.sample.app"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation(projects.sample.library)
  implementation(libs.androidActivityCompose)
  implementation(libs.androidMaterial)
  implementation(libs.composeMaterial)
  implementation(libs.composeUi)

  coreLibraryDesugaring(libs.coreLibraryDesugaring)
}
