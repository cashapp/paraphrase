pluginManagement {
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }

  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    mavenLocal()
  }
}

rootProject.name = "sample"
include(
  ":app",
  ":library",
)
includeBuild("..") {
  dependencySubstitution {
    substitute(module("app.cash.gingham:plugin")).using(project(":plugin"))
    substitute(module("app.cash.gingham:runtime:android")).using(project(":runtime:android"))
    substitute(module("app.cash.gingham:runtime:model")).using(project(":runtime:model"))
  }
}
