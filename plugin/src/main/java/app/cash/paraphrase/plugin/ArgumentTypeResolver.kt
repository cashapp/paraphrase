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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.Number as KotlinNumber
import kotlin.reflect.KClass
import kotlin.time.Duration as KotlinDuration

/**
 * Returns the final argument type for the given list of token types, or null if there is no
 * suitable argument type for the given combination of token types.
 *
 * For example:
 * - [Date] -> [LocalDate]
 * - [Date] + [Time] -> [LocalDateTime]
 * - [Date] + [Plural] -> null
 */
internal fun resolveArgumentType(tokenTypes: List<TokenType>): KClass<*>? =
  when (resolveCompatibleTokenType(tokenTypes)) {
    null -> null
    None -> Any::class
    Choice, Number, Plural, SpellOut -> KotlinNumber::class
    Date -> LocalDate::class
    Time -> LocalTime::class
    TimeWithOffset -> OffsetTime::class
    DateTime -> LocalDateTime::class
    DateTimeWithOffset -> OffsetDateTime::class
    DateTimeWithZone -> ZonedDateTime::class
    Offset -> ZoneOffset::class
    Duration -> KotlinDuration::class
    Ordinal, SelectOrdinal -> Int::class
    Select -> String::class
    NoArg -> Nothing::class
  }

private fun resolveCompatibleTokenType(tokens: List<TokenType>): TokenType? =
  tokens.reduceOrNull(::resolveCompatibleTokenType)

private fun resolveCompatibleTokenType(first: TokenType?, second: TokenType?): TokenType? =
  when {
    first == null || second == null -> null
    first == second || second.compatibleTypes.contains(first) -> first
    first.compatibleTypes.contains(second) -> second
    else -> first.compatibleTypes.firstOrNull { second.compatibleTypes.contains(it) }
  }

private val TokenType.compatibleTypes: List<TokenType>
  get() = compatibleTokenTypes[this]!!

/**
 * For a token of type A, tokens of type B are considered compatible if the argument type that
 * satisfies B also satisfies A.
 *
 * For example:
 * - [DateTime] is compatible with [Date], because [LocalDateTime] contains the date information
 *   required by [Date] tokens.
 * - [Date] is not compatible with [DateTime], because [LocalDate] does not contain the time
 *   information required by [DateTime] tokens.
 *
 * Compatible types are ordered from least restrictive to most restrictive. [ZonedDateTime] contains
 * a superset of the information in [DateTime], so it comes later in the list of types compatible
 * with [Date].
 */
private val compatibleTokenTypes: Map<TokenType, List<TokenType>> = mapOf(
  None to TokenType.values().asList(),
  Number to listOf(Choice, Ordinal, Plural, SelectOrdinal, SpellOut),
  Date to listOf(DateTime, DateTimeWithOffset, DateTimeWithZone),
  Time to listOf(DateTime, TimeWithOffset, DateTimeWithOffset, DateTimeWithZone),
  TimeWithOffset to listOf(DateTimeWithOffset, DateTimeWithZone),
  DateTime to listOf(DateTimeWithOffset, DateTimeWithZone),
  DateTimeWithOffset to listOf(DateTimeWithZone),
  DateTimeWithZone to emptyList(),
  Offset to listOf(TimeWithOffset, DateTimeWithOffset, DateTimeWithZone),
  SpellOut to listOf(Choice, Number, Ordinal, Plural, SelectOrdinal),
  Ordinal to listOf(SelectOrdinal),
  Duration to emptyList(),
  Choice to listOf(Number, Ordinal, Plural, SelectOrdinal, SpellOut),
  Plural to listOf(Choice, Number, Ordinal, SelectOrdinal, SpellOut),
  Select to emptyList(),
  SelectOrdinal to listOf(Ordinal),
  NoArg to emptyList(),
)
