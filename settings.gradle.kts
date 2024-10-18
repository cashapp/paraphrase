enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "paraphrase"
include(
  ":plugin",
  ":runtime",
  ":runtime-compose-ui",
  ":runtime-test",
  ":sample:app",
  ":sample:library",
  ":tests",
)

includeBuild("build-logic") {
  dependencySubstitution {
    substitute(module("app.cash.paraphrase:plugin")).using(project(":plugin"))
  }
}
