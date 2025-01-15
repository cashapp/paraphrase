@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.kotlinCompose)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.app"
  compileSdk = 35

  defaultConfig {
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }

  kotlinOptions {
    allWarningsAsErrors = true
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
