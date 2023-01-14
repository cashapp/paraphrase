// Copyright Square, Inc.
package app.cash.gingham.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

/**
 * A Gradle plugin that generates type checked formatters for patterned Android string resources.
 */
class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.run {
    extensions.getByType(AndroidComponentsExtension::class.java).onVariants { variant ->
      addRuntimeDependency()
      registerGenerateFormattedResourcesTask(variant)
    }
  }

  private fun Project.addRuntimeDependency() {
    val isInternal = properties["app.cash.gingham.internal"].toString() == "true"
    dependencies.add(
      "implementation",
      if (isInternal) "app.cash.gingham:runtime"
      else "app.cash.gingham:gingham-runtime:${BuildConfig.VERSION}"
    )
  }

  @Suppress("UnstableApiUsage")
  private fun Project.registerGenerateFormattedResourcesTask(variant: Variant) {
    val javaSources = variant.sources.java ?: return
    val resSources = variant.sources.res ?: return
    tasks.register(
      "generateFormattedResources${variant.name.capitalized()}",
      GenerateFormattedResources::class.java
    ).apply {
      javaSources.addGeneratedSourceDirectory(this, GenerateFormattedResources::outputDirectory)
      configure { task ->
        task.description = "Generates type-safe formatters for ${variant.name} string resources"
        task.namespace.set(variant.namespace)
        task.resourceDirectories.from(resSources.all)
        task.outputDirectory.set(File("$buildDir/generated/source/gingham/${variant.name}"))
      }
    }
  }
}
