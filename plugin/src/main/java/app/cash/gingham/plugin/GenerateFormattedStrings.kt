// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.generator.generateFormattedStringResources
import app.cash.gingham.plugin.model.TokenizedStringResource
import app.cash.gingham.plugin.parser.parseStringResources
import app.cash.icu.asIcuTokens
import app.cash.icu.tokens.Argument
import app.cash.icu.tokens.ChoiceArgument
import app.cash.icu.tokens.IcuToken
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

internal abstract class GenerateFormattedStrings @Inject constructor() : DefaultTask() {
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
      .flatMap { parseStringResources(it) }
      .map { stringResource ->
        TokenizedStringResource(
          name = stringResource.name,
          args = stringResource.text.asIcuTokens().findArguments()
        )
      }
      .filter { it.args.isNotEmpty() }
      .let { generateFormattedStringResources(packageName = namespace.get(), it) }
      .writeTo(outputDirectory.get().asFile)
  }

  private fun List<IcuToken>.findArguments(): Set<Argument> =
    flatMap { it.findArguments() }.toSet()

  private fun IcuToken.findArguments(): Set<Argument> =
    when (this) {
      !is Argument -> emptySet()
      !is ChoiceArgument<*> -> setOf(this)
      else -> setOf(this) + choices.flatMap { it.value.findArguments() }
    }

  private fun File.isStringResourceFile(): Boolean =
    isFile && bufferedReader().useLines { lines ->
      lines.any { it.trimStart().startsWith("<string ") }
    }
}
