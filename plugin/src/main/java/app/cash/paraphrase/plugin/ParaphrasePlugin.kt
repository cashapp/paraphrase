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

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTest
import com.android.build.api.variant.Sources
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.util.GradleVersion

/**
 * A Gradle plugin that generates type checked formatters for patterned Android string resources.
 */
public class ParaphrasePlugin : Plugin<Project> {
  override fun apply(target: Project): Unit =
    target.run {
      // If you update the minimum-supported Gradle version, check if the Kotlin api/language
      // version
      // can be bumped. See https://docs.gradle.org/current/userguide/compatibility.html#kotlin.
      val gradleMinimum = GradleVersion.version("9.0")
      val gradleCurrent = GradleVersion.current()
      require(gradleCurrent >= gradleMinimum) {
        "Plugin requires $gradleMinimum or newer. Found $gradleCurrent"
      }

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
    val runtimeDependency: Any =
      if (isInternal) {
        dependencies.project(mapOf("path" to ":runtime"))
      } else {
        "app.cash.paraphrase:paraphrase-runtime:${BuildConfig.VERSION}"
      }
    dependencies.add("api", runtimeDependency)

    // Automatically add the runtime Compose UI dependency if Compose is being used.
    afterEvaluate {
      val hasComposeFeature =
        extensions.getByType(CommonExtension::class.java).buildFeatures.compose == true
      val hasComposePlugin = pluginManager.hasPlugin("org.jetbrains.kotlin.plugin.compose")
      if (hasComposeFeature || hasComposePlugin) {
        val runtimeComposeUiDependency: Any =
          if (isInternal) {
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
    tasks
      .register(
        "generateFormattedResources${name.replaceFirstChar { it.uppercase() }}",
        GenerateFormattedResources::class.java,
      )
      .apply {
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
