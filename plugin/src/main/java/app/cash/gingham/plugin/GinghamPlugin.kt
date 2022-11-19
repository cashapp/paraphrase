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

class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.run {
    plugins.withId(ANDROID_APP_PLUGIN) {
      val extension = extensions.getByType(AppExtension::class.java)
      registerGenerateFormattedStringResourcesTasks(
        extension = extension,
        variants = extension.applicationVariants
      )
    }

    plugins.withId(ANDROID_LIBRARY_PLUGIN) {
      val extension = extensions.getByType(LibraryExtension::class.java)
      registerGenerateFormattedStringResourcesTasks(
        extension = extension,
        variants = extension.libraryVariants
      )
    }
  }

  private fun Project.registerGenerateFormattedStringResourcesTasks(
    extension: BaseExtension,
    variants: DomainObjectSet<out InternalBaseVariant>
  ) = variants.all { variant ->
    registerGenerateFormattedStringResourcesTask(
      extension = extension,
      variant = variant
    )
  }

  private fun Project.registerGenerateFormattedStringResourcesTask(
    extension: BaseExtension,
    variant: InternalBaseVariant
  ) = tasks.register(
    "generateFormattedStringResources${variant.name.capitalized()}",
    GenerateFormattedStringResources::class.java
  ).apply {
    val outputDirectory = File("${buildDir}/gingham/${variant.dirName}")
    variant.registerJavaGeneratingTask(this, outputDirectory)
    configure { task ->
      task.namespace.set(extension.namespace)
      task.resDirectories.from(variant.sourceSets.flatMap { it.resDirectories })
      task.outputDirectory.set(outputDirectory)
    }
  }
}