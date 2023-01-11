@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  kotlin("android")
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.mavenPublish)
}

android {
  namespace = "app.cash.gingham"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation(project(":runtime:model"))
}
