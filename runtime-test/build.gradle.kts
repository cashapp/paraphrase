plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.mavenPublish)
}

android {
  namespace = "app.cash.paraphrase.test"
  compileSdk = 34

  defaultConfig {
    minSdk = 24
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

dependencies {
  api(projects.runtime)

  api(libs.icu4j)

  coreLibraryDesugaring(libs.coreLibraryDesugaring)
}
