plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinCompose)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
}

android {
  namespace = "app.cash.paraphrase.compose"
  compileSdk = 35

  buildFeatures {
    compose = true
  }

  defaultConfig {
    minSdk = 24
  }
}

dependencies {
  api(libs.composeUi)
  api(projects.runtime)
}
