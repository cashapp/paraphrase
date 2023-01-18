@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidTest)
  id("app.cash.gingham")
}

android {
  namespace = "app.cash.gingham.tests"
  compileSdk = 33

  // This must point at an application module, although we won't use it.
  targetProjectPath = ":sample:app"
  // Our test APK runs independently of the target project APK.
  experimentalProperties["android.experimental.self-instrumenting"] = true

  defaultConfig {
    minSdk = 24
    targetSdk = 33

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

dependencies {
  implementation(libs.junit)
  implementation(libs.truth)
  implementation(libs.androidTestRunner)

  coreLibraryDesugaring(libs.coreLibraryDesugaring)
}
