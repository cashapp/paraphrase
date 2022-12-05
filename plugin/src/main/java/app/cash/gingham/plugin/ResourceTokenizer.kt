// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.StringResource
import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
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
import java.util.Date
import kotlin.reflect.KClass

/**
 * Parses the given resource and extracts the ICU argument tokens.
 */
internal fun tokenizeResource(stringResource: StringResource): TokenizedResource? {
  val pattern = try {
    MessagePattern(stringResource.text)
  } catch (throwable: Throwable) {
    return null
  }

  if (!pattern.hasNamedArguments() && !pattern.hasNumberedArguments()) {
    return null
  }

  val tokens = pattern.partsIterator()
    .asSequence()
    .withIndex()
    .filter { (_, part) -> part.type == ARG_START }
    .map { (index, part) ->
      pattern.getToken(
        identifier = pattern.getPart(index + 1),
        type = when (part.argType) {
          NONE -> Any::class
          SIMPLE -> when (pattern.getSubstring(pattern.getPart(index + 2)).lowercase()) {
            "date" -> Date::class
            "time" -> Date::class
            "number" -> Number::class
            else -> Any::class
          }

          CHOICE -> Int::class
          PLURAL -> Int::class
          SELECT -> String::class
          SELECTORDINAL -> Int::class
          else -> error("Unexpected argument type: ${part.argType.name}")
        }
      )
    }

  val deduplicatedTokens = buildMap {
    tokens.forEach { token ->
      if (!containsKey(token.key)) {
        put(token.key, token)
      }
    }
  }

  return TokenizedResource(
    name = stringResource.name,
    tokens = deduplicatedTokens.values.toList()
  )
}

private fun MessagePattern.getToken(identifier: Part, type: KClass<*>): Token =
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

private val Token.key: String
  get() = when (this) {
    is NamedToken -> name
    is NumberedToken -> number.toString()
  }
