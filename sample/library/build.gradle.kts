plugins {
  id("app.cash.gingham")
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "app.cash.gingham.sample.library"
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
