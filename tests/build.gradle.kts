@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidTest)
  alias(libs.plugins.kotlinAndroid)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.tests"
  compileSdk = 35

  // This must point at an application module, although we won't use it.
  targetProjectPath = ":sample:app"
  // Our test APK runs independently of the target project APK.
  experimentalProperties["android.experimental.self-instrumenting"] = true

  defaultConfig {
    minSdk = 24
    targetSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

dependencies {
  implementation(libs.junit)
  implementation(libs.assertk)
  implementation(libs.androidTestRunner)
  implementation(libs.testParameterInjector)

  coreLibraryDesugaring(libs.coreLibraryDesugaring)
}
