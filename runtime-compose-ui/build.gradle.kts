plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinCompose)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
}

android {
  namespace = "app.cash.paraphrase.compose"
}

dependencies {
  api(libs.composeUi)
  api(projects.runtime)
}
