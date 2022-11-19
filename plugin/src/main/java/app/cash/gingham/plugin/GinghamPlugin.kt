package app.cash.gingham.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val ANDROID_APP_PLUGIN = "com.android.application"
private const val ANDROID_LIBRARY_PLUGIN = "com.android.library"

class GinghamPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.run {
    plugins.withId(ANDROID_APP_PLUGIN) {
      registerGenerateFormattedStringResources(extensions.getByType(AppExtension::class.java))
    }

    plugins.withId(ANDROID_LIBRARY_PLUGIN) {
      registerGenerateFormattedStringResources(extensions.getByType(LibraryExtension::class.java))
    }
  }

  private fun Project.registerGenerateFormattedStringResources(extension: BaseExtension) =
    tasks.register(
      "generateFormattedStringResources",
      GenerateFormattedStringResources::class.java
    ).configure { task ->
      task.namespace.set(extension.namespace)
      task.resourceFiles.from(extension.sourceSets.flatMap { it.res.getSourceFiles() })
    }
}