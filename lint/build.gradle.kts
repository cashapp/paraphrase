import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.mavenPublish)
}

tasks.withType<KotlinJvmCompile> {compilerOptions { jvmTarget = JvmTarget.JVM_17 } }

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.named<Jar>("jar") {
  manifest {
    attributes(
      "Lint-Registry-v2" to "app.cash.paraphrase.lint.ParaphraseIssueRegistry",
    )
  }
}

dependencies {
  compileOnly(libs.lintApi)

  testImplementation(libs.junit)
  testImplementation(libs.lintChecks)
  testImplementation(libs.lintTests)
}
