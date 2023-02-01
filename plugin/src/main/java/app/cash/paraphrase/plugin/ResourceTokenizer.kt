// Copyright Square, Inc.
package app.cash.paraphrase.plugin

import app.cash.paraphrase.plugin.model.StringResource
import app.cash.paraphrase.plugin.model.TokenizedResource
import app.cash.paraphrase.plugin.model.TokenizedResource.Token
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NumberedToken
import com.ibm.icu.text.MessagePattern
import com.ibm.icu.text.MessagePattern.ArgType.CHOICE
import com.ibm.icu.text.MessagePattern.ArgType.NONE
import com.ibm.icu.text.MessagePattern.ArgType.PLURAL
import com.ibm.icu.text.MessagePattern.ArgType.SELECT
import com.ibm.icu.text.MessagePattern.ArgType.SELECTORDINAL
import com.ibm.icu.text.MessagePattern.ArgType.SIMPLE
import com.ibm.icu.text.MessagePattern.Part
import com.ibm.icu.text.MessagePattern.Part.Type.ARG_NAME
import com.ibm.icu.text.MessagePattern.Part.Type.ARG_NUMBER
import com.ibm.icu.text.MessagePattern.Part.Type.ARG_START

/**
 * Parses the given resource and extracts the ICU argument tokens.
 */
internal fun tokenizeResource(stringResource: StringResource): TokenizedResource {
  val pattern = try {
    MessagePattern(stringResource.text)
  } catch (throwable: Throwable) {
    return stringResource.toTokenizedResource(
      tokens = emptyList(),
      parsingError = throwable.message,
    )
  }

  if (!pattern.hasNamedArguments() && !pattern.hasNumberedArguments()) {
    return stringResource.toTokenizedResource(tokens = emptyList())
  }

  val tokens = pattern.partsIterator()
    .asSequence()
    .withIndex()
    .filter { (_, part) -> part.type == ARG_START }
    .map { (index, part) ->
      pattern.getToken(
        identifier = pattern.getPart(index + 1),
        type = when (part.argType) {
          NONE -> TokenType.None
          SIMPLE -> when (val simpleType = pattern.getSubstring(pattern.getPart(index + 2)).lowercase()) {
            "date" -> TokenType.Date
            "duration" -> TokenType.Duration
            "ordinal" -> TokenType.Ordinal
            "number" -> TokenType.Number
            "spellout" -> TokenType.SpellOut
            "time" -> TokenType.Time
            else -> error("Unexpected simple argument type: $simpleType")
          }
          CHOICE -> TokenType.Choice
          PLURAL -> TokenType.Plural
          SELECT -> TokenType.Select
          SELECTORDINAL -> TokenType.SelectOrdinal
          else -> error("Unexpected argument type: ${part.argType.name}")
        },
      )
    }

  return stringResource.toTokenizedResource(tokens = tokens.toList())
}

private fun StringResource.toTokenizedResource(tokens: List<Token>, parsingError: String? = null): TokenizedResource =
  TokenizedResource(name = name, description = description, tokens = tokens, parsingError = parsingError)

private fun MessagePattern.getToken(identifier: Part, type: TokenType): Token =
  when (identifier.type) {
    ARG_NAME -> NamedToken(name = getSubstring(identifier), type = type)
    ARG_NUMBER -> NumberedToken(number = identifier.value, type = type)
    else -> error("Unexpected identifier type: ${identifier.type.name}")
  }

private fun MessagePattern.partsIterator(): Iterator<Part> =
  object : Iterator<Part> {
    private var index = 0
    override fun hasNext(): Boolean = index < countParts()
    override fun next(): Part = getPart(index++)
  }

internal enum class TokenType {
  None,
  Number,
  Date,
  Time,
  SpellOut,
  Ordinal,
  Duration,
  Choice,
  Plural,
  Select,
  SelectOrdinal,
}
