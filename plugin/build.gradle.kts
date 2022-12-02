plugins {
  id("com.vanniktech.maven.publish")
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
  implementation("com.android.tools.build:gradle:7.2.1")
  implementation("com.ibm.icu:icu4j:72.1")
  implementation("com.squareup:kotlinpoet:1.12.0")
  testImplementation("com.google.truth:truth:1.1.3")
  testImplementation("junit:junit:4.13.2")
}
