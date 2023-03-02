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
import app.cash.paraphrase.plugin.TokenType.DateTime
import app.cash.paraphrase.plugin.TokenType.DateTimeWithOffset
import app.cash.paraphrase.plugin.TokenType.DateTimeWithZone
import app.cash.paraphrase.plugin.TokenType.Duration
import app.cash.paraphrase.plugin.TokenType.NoArg
import app.cash.paraphrase.plugin.TokenType.None
import app.cash.paraphrase.plugin.TokenType.Number
import app.cash.paraphrase.plugin.TokenType.Offset
import app.cash.paraphrase.plugin.TokenType.Ordinal
import app.cash.paraphrase.plugin.TokenType.Plural
import app.cash.paraphrase.plugin.TokenType.Select
import app.cash.paraphrase.plugin.TokenType.SelectOrdinal
import app.cash.paraphrase.plugin.TokenType.SpellOut
import app.cash.paraphrase.plugin.TokenType.Time
import app.cash.paraphrase.plugin.TokenType.TimeWithOffset
import app.cash.paraphrase.plugin.model.MergedResource
import app.cash.paraphrase.plugin.model.PublicResource
import app.cash.paraphrase.plugin.model.ResourceFolder
import app.cash.paraphrase.plugin.model.ResourceName
import app.cash.paraphrase.plugin.model.TokenizedResource
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NumberedToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.Number as KotlinNumber
import kotlin.time.Duration as KotlinDuration

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

  val arguments = defaultResource.tokens.map { token ->
    val argumentName = when (token) {
      is NamedToken -> token.name
      is NumberedToken -> "arg${token.number}"
    }
    val argumentKey = when (token) {
      is NamedToken -> token.name
      is NumberedToken -> token.number.toString()
    }
    val argumentType = when (token.type) {
      None -> Any::class
      Number, SpellOut -> KotlinNumber::class
      Date -> LocalDate::class
      Time -> LocalTime::class
      TimeWithOffset -> OffsetTime::class
      DateTime -> LocalDateTime::class
      DateTimeWithOffset -> OffsetDateTime::class
      // TODO: Handle NoArg?
      DateTimeWithZone, NoArg -> ZonedDateTime::class
      Offset -> ZoneOffset::class
      Duration -> KotlinDuration::class
      Choice, Ordinal, Plural, SelectOrdinal -> Int::class
      Select -> String::class
    }
    MergedResource.Argument(
      key = argumentKey,
      name = argumentName,
      type = argumentType,
    )
  }

  // TODO For now, we only take the first argument for each key.
  val deduplicatedArguments = buildMap {
    arguments.forEach { argument ->
      putIfAbsent(argument.name, argument)
    }
  }

  return MergedResource(
    name = name,
    description = defaultResource.description,
    visibility = publicResources.resolveVisibility(name = name, type = "string"),
    arguments = deduplicatedArguments.values.toList(),
    hasContiguousNumberedTokens = hasContiguousNumberedTokens,
    parsingErrors = emptyList(),
  )
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
