plugins {
  id("app.cash.gingham")
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "app.cash.gingham.sample.app"
  compileSdk = 33

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.3.1"
  }

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
  implementation(project(":library"))
  implementation("androidx.activity:activity-compose:1.5.1")
  implementation("androidx.compose.material:material:1.3.1")
  implementation("androidx.compose.ui:ui:1.3.1")
  implementation("com.google.android.material:material:1.7.0")
}
