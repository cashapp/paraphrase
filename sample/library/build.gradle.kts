@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  id("app.cash.paraphrase")
}

android {
  namespace = "app.cash.paraphrase.sample.library"
  compileSdk = 34

  defaultConfig {
    minSdk = 24
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }
}

androidComponents {
  beforeVariants {
    if (it.buildType == "release") {
      it.enable = false
    }
  }
}

dependencies {
  coreLibraryDesugaring(libs.coreLibraryDesugaring)

  testImplementation(projects.runtimeTest)

  testImplementation(libs.icu4j)
  testImplementation(libs.junit)
  testImplementation(libs.truth)
}
