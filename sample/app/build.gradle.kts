@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.app"
  compileSdk = 33

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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

androidComponents {
  beforeVariants {
    if (it.buildType == "release") {
      it.enable = false
    }
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
