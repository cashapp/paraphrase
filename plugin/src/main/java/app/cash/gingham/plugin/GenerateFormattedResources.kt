// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.generator.writeResources
import app.cash.gingham.plugin.parser.parseResources
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

internal abstract class GenerateFormattedResources @Inject constructor() : DefaultTask() {
  @get:Input
  abstract val namespace: Property<String>

  @get:InputFiles
  abstract val resDirectories: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun generateFormattedStringResources() {
    resDirectories
      .asFileTree
      .filter { it.isStringResourceFile() }
      .flatMap { parseResources(file = it) }
      .mapNotNull { tokenizeResource(stringResource = it) }
      .let { writeResources(packageName = namespace.get(), tokenizedResources = it) }
      .writeTo(directory = outputDirectory.get().asFile)
  }

  private fun File.isStringResourceFile(): Boolean =
    isFile && bufferedReader().useLines { lines ->
      lines.any { it.trimStart().startsWith("<string ") }
    }
}
