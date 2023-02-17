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
import com.ibm.icu.text.MessagePattern.Part.Type.ARG_STYLE

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
            "time" -> {
              val stylePart = pattern.getPart(index + 3)
              if (stylePart.type == ARG_STYLE) {
                when (pattern.getSubstring(stylePart).lowercase().trim()) {
                  "short", "medium" -> TokenType.Time
                  "long", "full" -> TokenType.TimeWithZone
                  else -> TokenType.TimeWithZone // TODO: https://github.com/cashapp/paraphrase/issues/92
                }
              } else {
                TokenType.Time
              }
            }
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
  TimeWithZone,
  SpellOut,
  Ordinal,
  Duration,
  Choice,
  Plural,
  Select,
  SelectOrdinal,
}
