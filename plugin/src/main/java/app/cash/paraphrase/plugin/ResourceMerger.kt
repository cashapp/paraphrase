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
import app.cash.paraphrase.plugin.model.PublicResource
import app.cash.paraphrase.plugin.model.ResourceFolder
import app.cash.paraphrase.plugin.model.ResourceName
import app.cash.paraphrase.plugin.model.TokenizedResource
import app.cash.paraphrase.plugin.model.TokenizedResource.Token
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NumberedToken

internal fun mergeResources(
  name: ResourceName,
  tokenizedResources: Map<ResourceFolder, TokenizedResource>,
  publicResources: Collection<PublicResource>,
): MergedResource? {
  // TODO For now, we only process strings in the default "values" folder.
  val defaultResource = tokenizedResources[ResourceFolder.Default] ?: return null

  val hasContiguousNumberedTokens = run {
    val argumentCount = defaultResource.tokens
      .mapTo(mutableSetOf()) {
        when (it) {
          is NamedToken -> it.name
          is NumberedToken -> it.number.toString()
        }
      }
      .size

    val tokenNumbers = defaultResource.tokens
      .filterIsInstance<NumberedToken>()
      .mapTo(mutableSetOf()) { it.number }

    (0 until argumentCount).toSet() == tokenNumbers
  }

  val deprecation = if (defaultResource.tokens.any { it.type == TokenType.Choice }) {
    MergedResource.Deprecation.WithMessage(
      message = """
        Use of the old 'choice' argument type is discouraged. Use a 'plural' argument to select
        sub-messages based on a numeric value, together with the plural rules for the specified
        language. Use a 'select' argument to select sub-messages via a fixed set of keywords.
      """.trimIndent().replace("\n", " "),
    )
  } else {
    MergedResource.Deprecation.None
  }
  val arguments = defaultResource.tokens
    .groupBy { it.argumentKey }
    .mapValues { (argumentKey, tokens) ->
      resolveArgumentType(tokens.map { it.type })?.let { argumentType ->
        MergedResource.Argument(
          key = argumentKey,
          name = tokens.first().argumentName,
          type = argumentType,
        )
      }
    }

  return MergedResource(
    name = name,
    description = defaultResource.description,
    visibility = publicResources.resolveVisibility(name = name, type = "string"),
    arguments = arguments.values.filterNotNull(),
    deprecation = deprecation,
    hasContiguousNumberedTokens = hasContiguousNumberedTokens,
    parsingErrors = arguments.filterValues { it == null }.keys.map {
      "Incompatible argument types for: $it"
    },
  )
}

private val Token.argumentKey: String
  get() = when (this) {
    is NamedToken -> name
    is NumberedToken -> number.toString()
  }

private val Token.argumentName: String
  get() = when (this) {
    is NamedToken -> name
    is NumberedToken -> "arg$number"
  }

/**
 * If no public resource declarations exist, then all resources are public. Otherwise, only those
 * declared public are public.
 */
private fun Collection<PublicResource>.resolveVisibility(
  name: ResourceName,
  type: String,
): MergedResource.Visibility {
  val public = isEmpty() || any { it is PublicResource.Named && it.type == type && it.name == name }
  return if (public) MergedResource.Visibility.Public else MergedResource.Visibility.Private
}
