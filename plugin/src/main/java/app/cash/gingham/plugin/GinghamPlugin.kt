// Copyright Square, Inc.
package app.cash.gingham.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTest
import com.android.build.api.variant.Sources
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.configurationcache.extensions.capitalized

/**
 * A Gradle plugin that generates type checked formatters for patterned Android string resources.
 */
@Suppress("UnstableApiUsage") // For 'Sources' type.
class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.run {
    addRuntimeDependency()
    extensions.getByType(AndroidComponentsExtension::class.java).onVariants { variant ->
      registerGenerateFormattedResourcesTask(
        sources = variant.sources,
        name = variant.name,
        namespace = variant.namespace,
      )

      (variant as? HasAndroidTest)?.androidTest?.let { androidTest ->
        registerGenerateFormattedResourcesTask(
          sources = androidTest.sources,
          name = androidTest.name,
          namespace = androidTest.namespace,
        )
      }
    }
  }

  private fun Project.addRuntimeDependency() {
    val isInternal = properties["app.cash.gingham.internal"].toString() == "true"
    val runtimeDependency: Any = if (isInternal) {
      dependencies.project(mapOf("path" to ":runtime"))
    } else {
      "app.cash.gingham:gingham-runtime:${BuildConfig.VERSION}"
    }
    dependencies.add("api", runtimeDependency)
  }

  private fun Project.registerGenerateFormattedResourcesTask(
    sources: Sources,
    name: String,
    namespace: Provider<String>,
  ) {
    val javaSources = sources.java ?: return
    val resSources = sources.res ?: return
    tasks.register(
      "generateFormattedResources${name.capitalized()}",
      GenerateFormattedResources::class.java
    ).apply {
      javaSources.addGeneratedSourceDirectory(this, GenerateFormattedResources::outputDirectory)
      configure { task ->
        task.description = "Generates type-safe formatters for $name string resources"
        task.namespace.set(namespace)
        task.resourceDirectories.from(resSources.all)
        task.outputDirectory.set(File("$buildDir/generated/source/gingham/$name"))
      }
    }
  }
}
