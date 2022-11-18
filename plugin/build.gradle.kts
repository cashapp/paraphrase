plugins {
  id("java-gradle-plugin")
  id("org.jetbrains.kotlin.jvm")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation("app.cash.icu:icu-message-parser:0.9.0")
  implementation("com.squareup:kotlinpoet:1.12.0")
}
