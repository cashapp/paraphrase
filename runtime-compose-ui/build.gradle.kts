plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
}

android {
  namespace = "app.cash.paraphrase.compose"
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
  }
}

dependencies {
  api(libs.composeUi)
  api(projects.runtime)
}
