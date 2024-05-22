plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.kotlinCompose)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
}

android {
  namespace = "app.cash.paraphrase.compose"
  compileSdk = 34

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
