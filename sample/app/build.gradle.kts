@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinCompose)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.app"

  defaultConfig {
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

kotlin {
  compilerOptions {
    allWarningsAsErrors = true
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
