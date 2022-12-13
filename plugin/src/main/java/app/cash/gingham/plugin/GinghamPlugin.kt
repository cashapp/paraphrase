// Copyright Square, Inc.
package app.cash.gingham.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.core.InternalBaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

/**
 * A Gradle plugin that generates type checked formatters for patterned Android string resources.
 */
class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.configureAndroidPlugin(
      androidPluginType = AppPlugin::class.java,
      androidExtensionType = AppExtension::class.java,
      getVariants = { applicationVariants }
    )

    target.configureAndroidPlugin(
      androidPluginType = LibraryPlugin::class.java,
      androidExtensionType = LibraryExtension::class.java,
      getVariants = { libraryVariants }
    )
  }

  private fun <T : BaseExtension> Project.configureAndroidPlugin(
    androidPluginType: Class<out Plugin<Project>>,
    androidExtensionType: Class<T>,
    getVariants: T.() -> DomainObjectSet<out InternalBaseVariant>
  ) = plugins.withType(androidPluginType) {
    val isInternalBuild = project.properties["app.cash.gingham.internal"].toString() == "true"
    if (isInternalBuild) {
      dependencies.add("implementation", "app.cash.gingham:runtime")
    } else {
      dependencies.add("implementation", "app.cash.gingham:gingham-runtime:${BuildConfig.VERSION}")
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
