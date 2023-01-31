// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.MergedResource
import app.cash.gingham.plugin.model.ResourceName
import com.google.common.truth.Truth.assertThat
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
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
        MergedResource(
          name = ResourceName("test2"),
          description = null,
          visibility = MergedResource.Visibility.Private,
          arguments = emptyList(),
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
          hasContiguousNumberedTokens = false,
          parsingErrors = emptyList(),
        ),
        MergedResource(
          name = ResourceName("test3"),
          description = null,
          visibility = MergedResource.Visibility.Private,
          arguments = emptyList(),
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
    val formattedResourcesObject = members
      .filterIsInstance<TypeSpec>()
      .find { it.name == "FormattedResources" }
    if (formattedResourcesObject == null) {
      fail("FormattedResources object not found")
    } else {
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
}
