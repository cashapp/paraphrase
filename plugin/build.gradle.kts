import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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
  packageName("app.cash.paraphrase.plugin")
  buildConfigField("String", "VERSION", "\"${project.version}\"")
  buildConfigField("String", "LIB_ANDROID_COLLECTION", "\"${libs.androidCollection.get()}\"")
}

gradlePlugin {
  plugins {
    create("paraphrase") {
      id = "app.cash.paraphrase"
      implementationClass = "app.cash.paraphrase.plugin.ParaphrasePlugin"
    }
  }
}

tasks.withType<KotlinJvmCompile> {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_11
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
  compileOnly(libs.agp)

  implementation(libs.icu4j)
  implementation(libs.kotlinPoet)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
}
