// Copyright Square, Inc.
package app.cash.gingham.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * A Gradle task that reads all of the Android string resources in a module and then generates
 * formatted resource methods for any that contain ICU arguments.
 */
@CacheableTask
internal abstract class GenerateFormattedResources @Inject constructor() : DefaultTask() {
  @get:Input
  abstract val namespace: Property<String>

  @get:Internal
  abstract val resourceDirectories: ConfigurableFileCollection

  @get:InputFiles
  @get:PathSensitive(RELATIVE)
  val stringResourceFiles: FileCollection
    get() = resourceDirectories
      .asFileTree
      .filter { it.isStringResourceFile() }

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun generateFormattedStringResources() {
    stringResourceFiles
      .flatMap { parseResources(file = it) }
      .map { tokenizeResource(rawResource = it) }
      .filter { it.tokens.isNotEmpty() }
      .takeIf { it.isNotEmpty() }
      ?.let { writeResources(packageName = namespace.get(), tokenizedResources = it) }
      ?.writeTo(directory = outputDirectory.get().asFile)
  }

  private fun File.isStringResourceFile(): Boolean =
    isFile && bufferedReader().useLines { lines ->
      lines.any { it.trimStart().startsWith("<string ") }
    }
}
