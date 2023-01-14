// Copyright Square, Inc.
package com.squareup.cash.gingham.plugin

import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor

/**
 * A Gradle task that reads all of the Android string resources in a module and then generates
 * formatted resource methods for any that contain ICU arguments.
 */
internal abstract class GenerateFormattedResources @Inject constructor() : DefaultTask() {
  @get:Input
  abstract val namespace: Property<String>

  @get:Internal
  abstract val resourceDirectories: ConfigurableFileCollection

  @get:InputFiles
  val stringResourceFiles: FileCollection
    get() = resourceDirectories
      .asFileTree
      .filter { it.isStringResourceFile() }

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @get:Inject
  abstract val workerExecutor: WorkerExecutor

  @TaskAction
  fun generateFormattedStringResources() {
    workerExecutor.noIsolation().submit(Action::class.java) { parameters ->
      parameters.namespace.set(namespace)
      parameters.stringResourceFiles.from(stringResourceFiles)
      parameters.outputDirectory.set(outputDirectory)
    }
  }

  private fun File.isStringResourceFile(): Boolean =
    isFile && bufferedReader().useLines { lines ->
      lines.any { it.trimStart().startsWith("<string ") }
    }

  internal interface Parameters : WorkParameters {
    val namespace: Property<String>
    val stringResourceFiles: ConfigurableFileCollection
    val outputDirectory: DirectoryProperty
  }

  internal abstract class Action : WorkAction<Parameters> {
    override fun execute() {
      parameters.stringResourceFiles
        .flatMap { parseResources(file = it) }
        .map { tokenizeResource(rawResource = it) }
        .filter { it.tokens.isNotEmpty() }
        .let { writeResources(packageName = parameters.namespace.get(), tokenizedResources = it) }
        .writeTo(directory = parameters.outputDirectory.get().asFile)
    }
  }
}
