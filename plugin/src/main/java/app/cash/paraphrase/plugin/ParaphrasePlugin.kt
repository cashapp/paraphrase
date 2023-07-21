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

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTest
import com.android.build.api.variant.Sources
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.configurationcache.extensions.capitalized

/**
 * A Gradle plugin that generates type checked formatters for patterned Android string resources.
 */
@Suppress("UnstableApiUsage") // For 'Sources' type.
public class ParaphrasePlugin : Plugin<Project> {
  override fun apply(target: Project): Unit = target.run {
    addDependencies()
    extensions.getByType(AndroidComponentsExtension::class.java).onVariants { variant ->
      registerGenerateFormattedResourcesTask(
        sources = variant.sources,
        name = variant.name,
        namespace = variant.namespace,
      )

      (variant as? HasAndroidTest)?.androidTest?.let { androidTest ->
        registerGenerateFormattedResourcesTask(
          sources = androidTest.sources,
          name = androidTest.name,
          namespace = androidTest.namespace,
        )
      }
    }
  }

  private fun Project.addDependencies() {
    val isInternal = properties["app.cash.paraphrase.internal"].toString() == "true"

    // Automatically add the runtime dependency.
    val runtimeDependency: Any = if (isInternal) {
      dependencies.project(mapOf("path" to ":runtime"))
    } else {
      "app.cash.paraphrase:paraphrase-runtime:${BuildConfig.VERSION}"
    }
    dependencies.add("api", runtimeDependency)

    // Automatically add the runtime Compose UI dependency if the Compose build feature is present.
    afterEvaluate {
      if (extensions.getByType(BaseExtension::class.java).buildFeatures.compose == true) {
        val runtimeComposeUiDependency: Any = if (isInternal) {
          dependencies.project(mapOf("path" to ":runtime-compose-ui"))
        } else {
          "app.cash.paraphrase:paraphrase-runtime-compose-ui:${BuildConfig.VERSION}"
        }
        dependencies.add("implementation", runtimeComposeUiDependency)
      }
    }

    // Automatically add the AndroidX Collection dependency for ArrayMap.
    dependencies.add("implementation", BuildConfig.LIB_ANDROID_COLLECTION)
  }

  private fun Project.registerGenerateFormattedResourcesTask(
    sources: Sources,
    name: String,
    namespace: Provider<String>,
  ) {
    val javaSources = sources.java ?: return
    val resSources = sources.res ?: return
    tasks.register(
      "generateFormattedResources${name.capitalized()}",
      GenerateFormattedResources::class.java,
    ).apply {
      javaSources.addGeneratedSourceDirectory(this, GenerateFormattedResources::outputDirectory)
      configure { task ->
        task.description = "Generates type-safe formatters for $name string resources"
        task.namespace.set(namespace)
        task.resourceDirectories.from(resSources.all)
        task.outputDirectory.set(layout.buildDirectory.dir("generated/source/paraphrase/$name"))
      }
    }
  }
}
