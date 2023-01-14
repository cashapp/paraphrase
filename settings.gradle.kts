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

rootProject.name = "gingham"
include(
  ":plugin",
  ":runtime",
  ":sample:app",
  ":sample:library",
)

includeBuild("build-logic") {
  dependencySubstitution {
    substitute(module("app.cash.gingham:plugin")).using(project(":plugin"))
  }
}
