@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `java-gradle-plugin`
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.mavenPublish)
}

buildConfig {
  useKotlinOutput {
    internalVisibility = true
  }
  packageName("app.cash.gingham.plugin")
  buildConfigField("String", "VERSION", "\"${project.version}\"")
}

gradlePlugin {
  plugins {
    create("gingham") {
      id = "app.cash.gingham"
      implementationClass = "app.cash.gingham.plugin.GinghamPlugin"
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  compileOnly(libs.agp)

  implementation(libs.icu4j)
  implementation(libs.kotlinPoet)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
}
