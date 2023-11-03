@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
  alias(libs.plugins.poko)
}

android {
  namespace = "app.cash.paraphrase"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

dependencies {
  api(libs.androidAnnotation)

  coreLibraryDesugaring(libs.coreLibraryDesugaring)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
}
