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
import assertk.assertThat
import assertk.assertions.isEqualTo
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
import org.junit.Test

class ArgumentTypeResolverTest {
  @Test
  fun resolveEmpty() {
    emptyList<TokenType>().assertArgumentType(null)
  }

  @Test
  fun resolveNone() {
    None.assertArgumentTypes { other -> resolveArgumentType(listOf(other)) }
  }

  @Test
  fun resolveNumber() {
    Number.assertArgumentTypes { other ->
      when (other) {
        None,
        Choice,
        Number,
        Plural,
        SpellOut -> KotlinNumber::class
        Ordinal,
        SelectOrdinal -> Long::class
        else -> null
      }
    }
  }

  @Test
  fun resolveDate() {
    Date.assertArgumentTypes { other ->
      when (other) {
        None,
        Date -> LocalDate::class
        Time,
        DateTime -> LocalDateTime::class
        Offset,
        TimeWithOffset,
        DateTimeWithOffset -> OffsetDateTime::class
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveTime() {
    Time.assertArgumentTypes { other ->
      when (other) {
        None,
        Time -> LocalTime::class
        Date,
        DateTime -> LocalDateTime::class
        Offset,
        TimeWithOffset -> OffsetTime::class
        DateTimeWithOffset -> OffsetDateTime::class
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveOffset() {
    Offset.assertArgumentTypes { other ->
      when (other) {
        None,
        Offset -> ZoneOffset::class
        Date,
        DateTime,
        DateTimeWithOffset -> OffsetDateTime::class
        Time,
        TimeWithOffset -> OffsetTime::class
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveDateTime() {
    DateTime.assertArgumentTypes { other ->
      when (other) {
        None,
        Date,
        Time,
        DateTime -> LocalDateTime::class
        Offset,
        TimeWithOffset,
        DateTimeWithOffset -> OffsetDateTime::class
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveTimeWithOffset() {
    TimeWithOffset.assertArgumentTypes { other ->
      when (other) {
        None,
        Time,
        Offset,
        TimeWithOffset -> OffsetTime::class
        Date,
        DateTime,
        DateTimeWithOffset -> OffsetDateTime::class
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveDateTimeWithOffset() {
    DateTimeWithOffset.assertArgumentTypes { other ->
      when (other) {
        None,
        Date,
        Time,
        Offset,
        DateTime,
        TimeWithOffset,
        DateTimeWithOffset -> OffsetDateTime::class
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveDateTimeWithZone() {
    DateTimeWithZone.assertArgumentTypes { other ->
      when (other) {
        None,
        Date,
        Time,
        Offset,
        DateTime,
        TimeWithOffset,
        DateTimeWithOffset,
        DateTimeWithZone -> ZonedDateTime::class
        else -> null
      }
    }
  }

  @Test
  fun resolveSpellOut() {
    SpellOut.assertArgumentTypes { other ->
      when (other) {
        None,
        Choice,
        Number,
        Plural,
        SpellOut -> KotlinNumber::class
        Ordinal,
        SelectOrdinal -> Long::class
        else -> null
      }
    }
  }

  @Test
  fun resolveOrdinal() {
    Ordinal.assertArgumentTypes { other ->
      when (other) {
        None,
        Choice,
        Number,
        Ordinal,
        Plural,
        SelectOrdinal,
        SpellOut -> Long::class
        else -> null
      }
    }
  }

  @Test
  fun resolveDuration() {
    Duration.assertArgumentTypes { other ->
      when (other) {
        None,
        Duration -> KotlinDuration::class
        else -> null
      }
    }
  }

  @Test
  fun resolveChoice() {
    Choice.assertArgumentTypes { other ->
      when (other) {
        None,
        Choice,
        Number,
        Plural,
        SpellOut -> KotlinNumber::class
        Ordinal,
        SelectOrdinal -> Long::class
        else -> null
      }
    }
  }

  @Test
  fun resolvePlural() {
    Plural.assertArgumentTypes { other ->
      when (other) {
        None,
        Choice,
        Number,
        Plural,
        SpellOut -> KotlinNumber::class
        Ordinal,
        SelectOrdinal -> Long::class
        else -> null
      }
    }
  }

  @Test
  fun resolveSelect() {
    Select.assertArgumentTypes { other ->
      when (other) {
        None,
        Select -> String::class
        else -> null
      }
    }
  }

  @Test
  fun resolveSelectOrdinal() {
    SelectOrdinal.assertArgumentTypes { other ->
      when (other) {
        None,
        Choice,
        Number,
        Ordinal,
        Plural,
        SelectOrdinal,
        SpellOut -> Long::class
        else -> null
      }
    }
  }

  @Test
  fun resolveNoArg() {
    NoArg.assertArgumentTypes { other ->
      when (other) {
        None,
        NoArg -> Nothing::class
        else -> null
      }
    }
  }

  private fun TokenType.assertArgumentTypes(expected: (TokenType) -> KClass<*>?) {
    listOf(this).assertArgumentType(expected(this))
    TokenType.values().forEach { other -> listOf(this, other).assertArgumentType(expected(other)) }
  }

  private fun List<TokenType>.assertArgumentType(expected: KClass<*>?) =
    assertThat(resolveArgumentType(tokenTypes = this)).isEqualTo(expected)
}
