// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.TokenType.Choice
import app.cash.gingham.plugin.TokenType.Date
import app.cash.gingham.plugin.TokenType.Duration
import app.cash.gingham.plugin.TokenType.None
import app.cash.gingham.plugin.TokenType.Number
import app.cash.gingham.plugin.TokenType.Ordinal
import app.cash.gingham.plugin.TokenType.Plural
import app.cash.gingham.plugin.TokenType.Select
import app.cash.gingham.plugin.TokenType.SelectOrdinal
import app.cash.gingham.plugin.TokenType.SpellOut
import app.cash.gingham.plugin.TokenType.Time
import app.cash.gingham.plugin.model.MergedResource
import app.cash.gingham.plugin.model.ResourceFolder
import app.cash.gingham.plugin.model.ResourceName
import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import java.time.Instant
import kotlin.Number as KotlinNumber
import kotlin.time.Duration as KotlinDuration

internal fun mergeResources(
  name: ResourceName,
  tokenizedResources: Map<ResourceFolder, TokenizedResource>,
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
      Number -> KotlinNumber::class
      Date, Time -> Instant::class
      Duration -> KotlinDuration::class
      Choice, Ordinal, Plural, SelectOrdinal, SpellOut -> Int::class
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
    arguments = deduplicatedArguments.values.toList(),
    hasContiguousNumberedTokens = hasContiguousNumberedTokens,
    parsingErrors = emptyList(),
  )
}
