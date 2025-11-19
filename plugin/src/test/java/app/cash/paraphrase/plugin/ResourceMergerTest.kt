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

import app.cash.paraphrase.plugin.TokenType.Choice
import app.cash.paraphrase.plugin.TokenType.Date
import app.cash.paraphrase.plugin.TokenType.Plural
import app.cash.paraphrase.plugin.TokenType.Time
import app.cash.paraphrase.plugin.model.MergedResource
import app.cash.paraphrase.plugin.model.MergedResource.Argument
import app.cash.paraphrase.plugin.model.MergedResource.Deprecation
import app.cash.paraphrase.plugin.model.PublicResource
import app.cash.paraphrase.plugin.model.ResourceFolder
import app.cash.paraphrase.plugin.model.ResourceName
import app.cash.paraphrase.plugin.model.TokenizedResource
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NumberedToken
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import java.time.LocalDateTime
import org.junit.Test

class ResourceMergerTest {

  @Test
  fun emptyPublicResourcesProducesPublicResource() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens = emptyList(),
                parsingError = null,
              )
          ),
        publicResources = emptyList(),
      )
    assertThat(result!!.visibility).isEqualTo(MergedResource.Visibility.Public)
    assertThat(result.deprecation).isEqualTo(Deprecation.None)
  }

  @Test
  fun inclusionInPublicResourcesProducesPublicResource() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens = emptyList(),
                parsingError = null,
              )
          ),
        publicResources = listOf(PublicResource.Named(name = ResourceName("test"), type = "string")),
      )
    assertThat(result!!.visibility).isEqualTo(MergedResource.Visibility.Public)
    assertThat(result.deprecation).isEqualTo(Deprecation.None)
  }

  @Test
  fun inclusionInPublicResourcesWithWrongTypeProducesPrivateResource() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens = emptyList(),
                parsingError = null,
              )
          ),
        publicResources = listOf(PublicResource.Named(name = ResourceName("test"), type = "color")),
      )
    assertThat(result!!.visibility).isEqualTo(MergedResource.Visibility.Private)
    assertThat(result.deprecation).isEqualTo(Deprecation.None)
  }

  @Test
  fun exclusionFromPublicResourcesProducesPrivateResource() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens = emptyList(),
                parsingError = null,
              )
          ),
        publicResources =
          listOf(
            PublicResource.EmptyDeclaration,
            PublicResource.Named(name = ResourceName("different"), type = "string"),
          ),
      )
    assertThat(result!!.visibility).isEqualTo(MergedResource.Visibility.Private)
    assertThat(result.deprecation).isEqualTo(Deprecation.None)
  }

  @Test
  fun tokensWithCompatibleTypesAreCombined() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens =
                  listOf(
                    NumberedToken(number = 0, type = Date),
                    NumberedToken(number = 0, type = Time),
                  ),
                parsingError = null,
              )
          ),
        publicResources = emptyList(),
      )
    assertThat(result!!.arguments)
      .containsExactly(Argument(key = "0", name = "arg0", type = LocalDateTime::class))
    assertThat(result.parsingErrors).isEmpty()
    assertThat(result.deprecation).isEqualTo(Deprecation.None)
  }

  @Test
  fun tokensWithIncompatibleTypesReportsParsingError() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens =
                  listOf(
                    NumberedToken(number = 0, type = Date),
                    NumberedToken(number = 0, type = Plural),
                  ),
                parsingError = null,
              )
          ),
        publicResources = emptyList(),
      )
    assertThat(result!!.arguments).isEmpty()
    assertThat(result.parsingErrors).containsExactly("Incompatible argument types for: 0")
    assertThat(result.deprecation).isEqualTo(Deprecation.None)
  }

  @Test
  fun choiceArgumentProducesDeprecatedFunction() {
    val result =
      mergeResources(
        name = ResourceName("test"),
        tokenizedResources =
          mapOf(
            ResourceFolder.Default to
              TokenizedResource(
                name = ResourceName("test"),
                description = null,
                tokens =
                  listOf(
                    NamedToken(name = "choice", type = Choice),
                    NamedToken(name = "other", type = Date),
                  ),
                parsingError = null,
              )
          ),
        publicResources = emptyList(),
      )

    val deprecation = result!!.deprecation as Deprecation.WithMessage
    assertThat(deprecation.message).contains("Use of the old 'choice' argument type is discouraged")
  }
}
