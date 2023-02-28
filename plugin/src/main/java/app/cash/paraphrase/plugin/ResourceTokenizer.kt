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
            "date" -> {
              val stylePart = pattern.getPart(index + 3)
              if (stylePart.type == ARG_STYLE) {
                val style = pattern.getSubstring(stylePart).trim()
                when (style.lowercase()) {
                  "short", "medium", "long", "full" -> TokenType.Date
                  else -> getTokenType(dateTimeFormatPattern = style)
                }
              } else {
                TokenType.Date
              }
            }
            "duration" -> TokenType.Duration
            "ordinal" -> TokenType.Ordinal
            "number" -> TokenType.Number
            "spellout" -> TokenType.SpellOut
            "time" -> {
              val stylePart = pattern.getPart(index + 3)
              if (stylePart.type == ARG_STYLE) {
                val style = pattern.getSubstring(stylePart).trim()
                when (style.lowercase()) {
                  "short", "medium" -> TokenType.Time
                  "long", "full" -> TokenType.DateTimeWithZoneId
                  else -> getTokenType(dateTimeFormatPattern = style)
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
  DateWithZoneOffset,
  DateWithZoneId,
  Time,
  TimeWithZoneOffset,
  DateTime,
  DateTimeWithZoneOffset,
  DateTimeWithZoneId,
  ZoneOffset,
  SpellOut,
  Ordinal,
  Duration,
  Choice,
  Plural,
  Select,
  SelectOrdinal,
  NoArg,
}

private fun getTokenType(dateTimeFormatPattern: String): TokenType {
  var hasDate = false
  var hasTime = false
  var hasZoneOffset = false
  var hasZoneId = false
  for (patternItem in dateTimeFormatPattern.getDateTimeSymbols()) {
    if (patternItem in DateSymbols) hasDate = true
    if (patternItem in TimeSymbols) hasTime = true
    if (patternItem in ZoneOffsetSymbols) hasZoneOffset = true
    if (patternItem in ZoneIdSymbols) hasZoneId = true

    // Break if we already satisfy the highest-priority condition:
    if (hasDate && hasTime && hasZoneId) break
  }

  return when {
    hasDate && hasTime && hasZoneId -> TokenType.DateTimeWithZoneId
    hasDate && hasTime && hasZoneOffset -> TokenType.DateTimeWithZoneOffset
    hasDate && hasTime -> TokenType.DateTime
    hasDate && hasZoneId -> TokenType.DateWithZoneId
    hasDate && hasZoneOffset -> TokenType.DateWithZoneOffset
    hasDate -> TokenType.Date
    hasTime && hasZoneId -> TokenType.DateTimeWithZoneId
    hasTime && hasZoneOffset -> TokenType.TimeWithZoneOffset
    hasTime -> TokenType.Time
    hasZoneId -> TokenType.DateWithZoneId
    hasZoneOffset -> TokenType.ZoneOffset
    else -> TokenType.NoArg
  }
}

//region Date/time format symbols
// https://unicode-org.github.io/icu/userguide/format_parse/datetime/#date-field-symbol-table

// Adapted from android.icu.text.SimpleDateFormat.getPatternItems
//  https://cs.android.com/android/platform/superproject/+/master:external/icu/android_icu4j/src/main/java/android/icu/text/SimpleDateFormat.java;l=2146
private fun String.getDateTimeSymbols(): List<Char> {
  var isPrevQuote = false
  var inQuote = false
  val text = StringBuilder()
  var itemType = Char(0)
  var itemLength = 1

  val items = mutableListOf<Char>()

  forEach { ch ->
    if (ch == '\'') {
      if (isPrevQuote) {
        text.append(ch)
        isPrevQuote = false
      } else {
        isPrevQuote = true
        if (itemType != Char(0)) {
          items.add(itemType)
          itemType = Char(0)
        }
      }
      inQuote = !inQuote
    } else {
      isPrevQuote = false
      if (inQuote) {
        text.append(ch)
      } else {
        if (ch.isDateTimeFormatSymbol) {
          // a date/time pattern character
          if (ch == itemType) {
            itemLength++
          } else {
            if (itemType == Char(0)) {
              if (text.isNotEmpty()) {
                // Skip adding string literals to the pattern items list
                text.setLength(0)
              }
            } else {
              items.add(itemType)
            }
            itemType = ch
            itemLength = 1
          }
        } else {
          // a string literal
          if (itemType != Char(0)) {
            items.add(itemType)
            itemType = Char(0)
          }
          text.append(ch)
        }
      }
    }
  }
  // handle last item
  if (itemType == Char(0)) {
    if (text.isNotEmpty()) {
      // Skip adding string literals to the pattern items list
      text.setLength(0)
    }
  } else {
    items.add(itemType)
  }

  return items.filter { it != Char(0) }
}

private val DateSymbols = setOf(
  'G', // era designator
  'y', // year
  'Y', // year of "Week of Year"
  'u', // extended year
  'U', // cyclic year name, as in Chinese lunar calendar
  'r', // related Gregorian year
  'Q', // quarter
  'q', // stand-alone quarter
  'M', // month in year
  'L', // stand-alone month in year
  'w', // week of year
  'W', // week of month
  'd', // day in month
  'D', // day of year
  'F', // day of week in month
  'g', // modified julian day
  'E', // day of week
  'e', // local day of week (example: if Monday is 1st day, Tuesday is 2nd)
  'c', // stand-alone local day of week
)

private val TimeSymbols = setOf(
  'a', // AM or PM
  'b', // am, pm, noon, midnight
  'B', // flexible day periods
  'h', // hour in am/pm (1~12)
  'H', // hour in day (0~23)
  'k', // hour in day (1~24)
  'K', // hour in am/pm (0~11)
  'm', // minute in hour
  's', // second in minute
  'S', // fractional second - truncates/appends zeros to the count of letters when formatting
  'A', // milliseconds in day
)

/**
 * Time zone formats that only depict an offset from GMT, and thus require only a
 * [java.time.ZoneOffset].
 */
private val ZoneOffsetSymbols = setOf(
  'Z', // ISO8601 basic/extended hms? / long localized GMT
  'O', // short/long localized GMT
  'X', // ISO8601 variants, with Z for 0
  'x', // ISO8601 variants, without Z for 0
)

/**
 * Time zone formats that depict a named time zone, and thus require a [java.time.ZoneId].
 */
private val ZoneIdSymbols = setOf(
  'z', // specific non-location
  'v', // generic non-location (falls back first to VVVV)
  'V', // short/long time zone ID / exemplar city / generic location (falls back to OOOO)
)

private val Char.isDateTimeFormatSymbol: Boolean
  get() = this in DateSymbols ||
    this in TimeSymbols ||
    this in ZoneOffsetSymbols ||
    this in ZoneIdSymbols
//endregion
