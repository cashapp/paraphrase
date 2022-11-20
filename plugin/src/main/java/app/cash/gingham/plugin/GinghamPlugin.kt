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

class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.run {
    configureAndroidPlugin(
      id = ANDROID_APP_PLUGIN,
      extensionType = AppExtension::class.java,
      getVariants = { applicationVariants }
    )

    configureAndroidPlugin(
      id = ANDROID_LIBRARY_PLUGIN,
      extensionType = LibraryExtension::class.java,
      getVariants = { libraryVariants }
    )
  }

  private fun <T: BaseExtension> Project.configureAndroidPlugin(
    id: String,
    extensionType: Class<T>,
    getVariants: T.() -> DomainObjectSet<out InternalBaseVariant>
  ) {
    plugins.withId(id) {
      dependencies.add("implementation", GINGHAM_RUNTIME)
      val extension = extensions.getByType(extensionType)
      extension.getVariants().all { variant ->
        registerGenerateFormattedStringResourcesTask(
          extension = extension,
          variant = variant
        )
      }
    }
  }

  private fun Project.registerGenerateFormattedStringResourcesTask(
    extension: BaseExtension,
    variant: InternalBaseVariant
  ) = tasks.register(
    "generateFormattedStringResources${variant.name.capitalized()}",
    GenerateFormattedStrings::class.java
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