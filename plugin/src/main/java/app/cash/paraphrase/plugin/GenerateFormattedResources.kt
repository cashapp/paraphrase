/*
 * Copyright (C) 2023 Cash App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paraphrase.plugin

import app.cash.paraphrase.plugin.model.ResourceFolder
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task that reads all of the Android string resources in a module and then generates
 * formatted resource methods for any that contain ICU arguments.
 */
@CacheableTask
internal abstract class GenerateFormattedResources @Inject constructor() : DefaultTask() {
  @get:Input
  abstract val namespace: Property<String>

  @get:InputFiles
  @get:PathSensitive(RELATIVE)
  abstract val resourceDirectories: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun generateFormattedStringResources() {
    // Extract the 'values'-style directories from each resource directory.
    val valuesFolders = resourceDirectories.files
      .flatMap { it.listFiles().orEmpty().toList() }
      .filter { it.name == "values" || it.name.startsWith("values-") }

    // Turn each resource folder into a map of its name to its files.
    //
    // Example:
    //   values -> [strings.xml, dimens.xml]
    //   values-es -> [strings.xml]
    val filesByConfiguration = valuesFolders
      .associate { folder ->
        ResourceFolder(folder.name) to folder.listFiles().orEmpty().toList()
      }

    // Parse the files in each folder into the tokenized resources.
    //
    // Example:
    //   values -> [TokenizedResource(name=hi, ..), TokenizedResource(name=hello, ..)]
    //   values-es -> [TokenizedResource(name=hello, ..)]
    val resourcesByConfiguration = filesByConfiguration
      .mapValues { (_, files) ->
        files.flatMap(::parseResources).map(::tokenizeResource)
      }

    // Split the folder map into individual maps keyed on resource name.
    //
    // Example:
    //   hello -> { values -> TokenizedResource(..)
    //              values-es -> TokenizedResource(..) }
    //   hi -> { values -> TokenizedResource(..) }
    val resourceConfigurationsByName = resourcesByConfiguration
      .flatMap { (key, resources) ->
        resources.map { resource ->
          key to resource
        }
      }
      .groupBy { (_, resource) ->
        resource.name
      }
      .mapValues { (_, value) ->
        value.toMap()
      }

    // Parse the files in each folder into a set of public resource declarations.
    // TODO: Can limit parsing to only public.xml? The wording used at
    //  https://developer.android.com/studio/projects/android-library#PrivateResources suggests this
    //  is the case. Check AGP source.
    val publicResources = (filesByConfiguration[ResourceFolder.Default] ?: emptyList())
      .flatMap(::parsePublicResources)
      .toSet()

    // Merge each resource's configuration map into final, canonical versions.
    val mergedResources = resourceConfigurationsByName
      .mapNotNull { (name, resourceByConfiguration) ->
        mergeResources(name, resourceByConfiguration, publicResources)
      }
      .filter { it.arguments.isNotEmpty() }

    writeResources(namespace.get(), mergedResources)
      .writeTo(outputDirectory.get().asFile)

    // TODO Fail on errors which make it this far.
  }
}
