plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "app.cash.gingham.sample.app"
  compileSdk = 33

  defaultConfig {
    applicationId = "app.cash.gingham.sample.app"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"
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
  implementation("com.google.android.material:material:1.7.0")
}