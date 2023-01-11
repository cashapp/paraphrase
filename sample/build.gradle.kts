plugins {
  kotlin("android") version libs.versions.kotlin apply false
  kotlin("jvm") version libs.versions.kotlin apply false
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
}

buildscript {
  dependencies {
    classpath("com.squareup.cash.gingham:plugin")
  }
}
