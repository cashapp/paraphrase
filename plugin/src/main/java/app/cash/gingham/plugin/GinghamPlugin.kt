// Copyright Square, Inc.
package app.cash.gingham.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.core.InternalBaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

private const val ANDROID_APP_PLUGIN = "com.android.application"
private const val ANDROID_LIBRARY_PLUGIN = "com.android.library"
private const val GINGHAM_RUNTIME = "app.cash.gingham:runtime"

/**
 * A Gradle plugin that generates type checked formatters for patterned Android string resources.
 */
class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.run {
    configureAndroidPlugin(
      androidPluginId = ANDROID_APP_PLUGIN,
      androidExtensionType = AppExtension::class.java,
      getVariants = { applicationVariants }
    )

    configureAndroidPlugin(
      androidPluginId = ANDROID_LIBRARY_PLUGIN,
      androidExtensionType = LibraryExtension::class.java,
      getVariants = { libraryVariants }
    )
  }

  private fun <T : BaseExtension> Project.configureAndroidPlugin(
    androidPluginId: String,
    androidExtensionType: Class<T>,
    getVariants: T.() -> DomainObjectSet<out InternalBaseVariant>
  ) = plugins.withId(androidPluginId) {
    val isInternalBuild = project.properties["app.cash.gingham.internal"].toString() == "true"
    if (isInternalBuild) {
      dependencies.add("implementation", GINGHAM_RUNTIME)
    } else {
      dependencies.add("implementation", "app.cash.gingham:gingham-runtime:0.1.0")
    }

    extensions.getByType(androidExtensionType).getVariants().all { variant ->
      registerGenerateFormattedResourcesTask(variant = variant)
    }
  }

  private fun Project.registerGenerateFormattedResourcesTask(
    variant: InternalBaseVariant
  ) = tasks.register(
    "generateFormattedResources${variant.name.capitalized()}",
    GenerateFormattedResources::class.java
  ).apply {
    val outputDirectory = File("$buildDir/gingham/${variant.dirName}")
    variant.registerJavaGeneratingTask(this, outputDirectory)
    configure { task ->
      task.namespace.set(variant.applicationId)
      task.resourceDirectories.from(variant.sourceSets.flatMap { it.resDirectories })
      task.outputDirectory.set(outputDirectory)
    }
  }
}
