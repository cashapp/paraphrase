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

import app.cash.paraphrase.plugin.model.MergedResource
import app.cash.paraphrase.plugin.model.MergedResource.Deprecation
import app.cash.paraphrase.plugin.model.ResourceName
import com.google.common.truth.Truth.assertThat
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.junit.Assert.fail
import org.junit.Test

class ResourceWriterTest {

  @Test
  fun publicResourceGetsPublicFunction() {
    val result = writeResources(
      packageName = "com.example",
      mergedResources = listOf(
        MergedResource(
          name = ResourceName("test"),
          description = null,
          visibility = MergedResource.Visibility.Public,
          arguments = emptyList(),
          deprecation = Deprecation.None,
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
      ),
    )

    result.assertVisibility(
      expectedClassVisibility = KModifier.PUBLIC,
      "test" to KModifier.PUBLIC,
    )
  }

  @Test
  fun privateResourceGetsInternalFunction() {
    val result = writeResources(
      packageName = "com.example",
      mergedResources = listOf(
        MergedResource(
          name = ResourceName("test1"),
          description = null,
          visibility = MergedResource.Visibility.Public,
          arguments = emptyList(),
          deprecation = Deprecation.None,
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
        MergedResource(
          name = ResourceName("test2"),
          description = null,
          visibility = MergedResource.Visibility.Private,
          arguments = emptyList(),
          deprecation = Deprecation.None,
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
      ),
    )

    result.assertVisibility(
      expectedClassVisibility = KModifier.PUBLIC,
      "test1" to KModifier.PUBLIC,
      "test2" to KModifier.INTERNAL,
    )
  }

  @Test
  fun onlyPrivateResourcesProduceInternalObject() {
    val result = writeResources(
      packageName = "com.example",
      mergedResources = listOf(
        MergedResource(
          name = ResourceName("test2"),
          description = null,
          visibility = MergedResource.Visibility.Private,
          arguments = emptyList(),
          deprecation = Deprecation.None,
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
        MergedResource(
          name = ResourceName("test3"),
          description = null,
          visibility = MergedResource.Visibility.Private,
          arguments = emptyList(),
          deprecation = Deprecation.None,
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
      ),
    )

    result.assertVisibility(
      expectedClassVisibility = KModifier.INTERNAL,
      "test2" to KModifier.INTERNAL,
      "test3" to KModifier.INTERNAL,
    )
  }

  private fun FileSpec.assertVisibility(
    expectedClassVisibility: KModifier,
    vararg expectedFunctionVisibility: Pair<String, KModifier>,
  ) {
    assertOnFormattedResourcesObject { formattedResourcesObject ->
      assertThat(formattedResourcesObject.modifiers).contains(expectedClassVisibility)

      expectedFunctionVisibility.forEach { (name, expectedVisibility) ->
        val function = formattedResourcesObject.funSpecs.find { it.name == name }
        if (function == null) {
          fail("Function with name <$name> not found")
        } else {
          assertThat(function.modifiers).contains(expectedVisibility)
        }
      }
    }
  }

  @Test
  fun deprecationWithMessageProducesDeprecationWithMessage() {
    val result = writeResources(
      packageName = "com.example",
      mergedResources = listOf(
        MergedResource(
          name = ResourceName("testFun"),
          description = null,
          visibility = MergedResource.Visibility.Public,
          arguments = emptyList(),
          deprecation = Deprecation.WithMessage("Test message"),
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
      ),
    )

    result.assertOnFormattedResourcesObject { formattedResourcesObject ->
      val testFun = formattedResourcesObject.funSpecs.single { it.name == "testFun" }
      assertThat(testFun.annotations).contains(
        AnnotationSpec.builder(Deprecated::class)
          .addMember("%S", "Test message")
          .build(),
      )
    }
  }

  private inline fun FileSpec.assertOnFormattedResourcesObject(
    block: (formattedResourcesObject: TypeSpec) -> Unit,
  ) {
    val formattedResourcesObject = members
      .filterIsInstance<TypeSpec>()
      .find { it.name == "FormattedResources" }
    if (formattedResourcesObject == null) {
      fail("FormattedResources object not found")
    } else {
      block(formattedResourcesObject)
    }
  }
}
