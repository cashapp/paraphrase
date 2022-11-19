plugins {
  id("java-gradle-plugin")
  id("org.jetbrains.kotlin.jvm") version "1.7.10"
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
  implementation("app.cash.icu:icu-message-parser:0.9.0")
  implementation("com.android.tools.build:gradle:7.2.1")
  implementation("com.squareup:kotlinpoet:1.12.0")
}
