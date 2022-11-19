pluginManagement {
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven { setUrl("https://jitpack.io") }
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven { setUrl("https://jitpack.io") }
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
    substitute(module("app.cash.gingham:runtime")).using(project(":runtime"))
  }
}
