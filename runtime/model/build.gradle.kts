@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `java-library`
  kotlin("jvm")
  alias(libs.plugins.mavenPublish)
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation(libs.androidAnnotation)
}
