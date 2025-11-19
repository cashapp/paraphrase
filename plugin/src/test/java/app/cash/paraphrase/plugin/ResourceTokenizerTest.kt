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

import app.cash.paraphrase.plugin.TokenType.Date
import app.cash.paraphrase.plugin.TokenType.DateTime
import app.cash.paraphrase.plugin.TokenType.DateTimeWithOffset
import app.cash.paraphrase.plugin.TokenType.DateTimeWithZone
import app.cash.paraphrase.plugin.TokenType.NoArg
import app.cash.paraphrase.plugin.TokenType.None
import app.cash.paraphrase.plugin.TokenType.Number
import app.cash.paraphrase.plugin.TokenType.Offset
import app.cash.paraphrase.plugin.TokenType.Plural
import app.cash.paraphrase.plugin.TokenType.Select
import app.cash.paraphrase.plugin.TokenType.SelectOrdinal
import app.cash.paraphrase.plugin.TokenType.Time
import app.cash.paraphrase.plugin.TokenType.TimeWithOffset
import app.cash.paraphrase.plugin.model.ResourceName
import app.cash.paraphrase.plugin.model.StringResource
import app.cash.paraphrase.plugin.model.TokenizedResource
import app.cash.paraphrase.plugin.model.TokenizedResource.Token
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.paraphrase.plugin.model.TokenizedResource.Token.NumberedToken
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class ResourceTokenizerTest {
  @Test
  fun tokenizeResourceWithNoArguments() {
    "Test".assertTokens()
  }

  @Test
  fun tokenizeResourceWithNamedSimpleTokens() {
    "Test {test} {test_number, number} {test_date, date} {test_time, time}"
      .assertTokens(
        NamedToken(name = "test", type = None),
        NamedToken(name = "test_number", type = Number),
        NamedToken(name = "test_date", type = Date),
        NamedToken(name = "test_time", type = Time),
      )
  }

  @Test
  fun tokenizeResourceWithNumberedSimpleTokens() {
    "Test {0} {1, number} {2, date} {3, time}"
      .assertTokens(
        NumberedToken(number = 0, type = None),
        NumberedToken(number = 1, type = Number),
        NumberedToken(number = 2, type = Date),
        NumberedToken(number = 3, type = Time),
      )
  }

  @Test
  fun tokenizeResourceWithNamedPluralArgument() {
    """
    {test, plural,
      zero {Test 0 {test_nested}}
      one {Test 1 {test_nested}}
      other {Test # {test_nested}}
    }
    """
      .trimIndent()
      .assertTokens(
        NamedToken(name = "test", type = Plural),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
      )
  }

  @Test
  fun tokenizeResourceWithNumberedPluralArgument() {
    """
    {0, plural,
      zero {Test 0 {1}}
      one {Test 1 {1}}
      other {Test # {1}}
    }
    """
      .trimIndent()
      .assertTokens(
        NumberedToken(number = 0, type = Plural),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
      )
  }

  @Test
  fun tokenizeResourceWithNamedSelectArgument() {
    """
    {test, select,
      red {Test red {test_nested}}
      green {Test green {test_nested}}
      blue {Test blue {test_nested}}
      other {Test other {test_nested}}
    }
    """
      .trimIndent()
      .assertTokens(
        NamedToken(name = "test", type = Select),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
      )
  }

  @Test
  fun tokenizeResourceWithNumberedSelectArgument() {
    """
    {0, select,
      red {Test red {1}}
      green {Test green {1}}
      blue {Test blue {1}}
      other {Test other {1}}
    }
    """
      .trimIndent()
      .assertTokens(
        NumberedToken(number = 0, type = Select),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
      )
  }

  @Test
  fun tokenizeResourceWithNamedSelectOrdinalArgument() {
    """
    {test, selectordinal,
      zero {Test 0 {test_nested}}
      one {Test 1 {test_nested}}
      other {Test # {test_nested}}
    }
    """
      .trimIndent()
      .assertTokens(
        NamedToken(name = "test", type = SelectOrdinal),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
        NamedToken(name = "test_nested", type = None),
      )
  }

  @Test
  fun tokenizeResourceWithNumberedSelectOrdinalArgument() {
    """
    {0, selectordinal,
      zero {Test 0 {1}}
      one {Test 1 {1}}
      other {Test # {1}}
    }
    """
      .trimIndent()
      .assertTokens(
        NumberedToken(number = 0, type = SelectOrdinal),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
        NumberedToken(number = 1, type = None),
      )
  }

  @Test
  fun tokenizeResourceWithReusedNamedChoiceArgument() {
    """
    {count, plural,
      zero {Test 0 {count}}
      one {Test 1 {count}}
      other {Test # {count}}
    }
    """
      .trimIndent()
      .assertTokens(
        NamedToken(name = "count", type = Plural),
        NamedToken(name = "count", type = None),
        NamedToken(name = "count", type = None),
        NamedToken(name = "count", type = None),
      )
  }

  @Test
  fun tokenizeResourceWithReusedNumberedChoiceArgument() {
    """
    {0, plural,
      zero {Test 0 {0}}
      one {Test 1 {0}}
      other {Test # {0}}
    }
    """
      .trimIndent()
      .assertTokens(
        NumberedToken(number = 0, type = Plural),
        NumberedToken(number = 0, type = None),
        NumberedToken(number = 0, type = None),
        NumberedToken(number = 0, type = None),
      )
  }

  @Test
  fun tokenizeResourceWithDateFormat() {
    """
    Test
    {short, date, short}
    {medium, date, medium}
    {long, date, long}
    {full, date, full}
    """
      .trimIndent()
      .assertTokens(
        NamedToken(name = "short", type = Date),
        NamedToken(name = "medium", type = Date),
        NamedToken(name = "long", type = Date),
        NamedToken(name = "full", type = Date),
      )
  }

  @Test
  fun tokenizeResourceWithTimeFormat() {
    """
    Test
    {short, time, short}
    {medium, time, medium}
    {long, time, long}
    {full, time, full}
    """
      .trimIndent()
      .assertTokens(
        NamedToken(name = "short", type = Time),
        NamedToken(name = "medium", type = Time),
        NamedToken(name = "long", type = DateTimeWithZone),
        NamedToken(name = "full", type = DateTimeWithZone),
      )
  }

  @Test
  fun tokenizeResourceWithDateTimeFormatPattern() {
    for (type in setOf("date", "time")) {
      """
        Test
        {date_time_id, $type, yaz}
        {date_time_offset, $type, MbZ}
        {date_time, $type, Lh}
        {date_id, $type, wz}
        {date_offset, $type, WO}
        {date, $type, d}
        {time_id, $type, Hv}
        {time_offset, $type, mX}
        {time, $type, s}
        {id, $type, V}
        {offset, $type, x}
        {no_arg, $type, 'yaz'}
      """
        .trimIndent()
        .assertTokens(
          NamedToken(name = "date_time_id", type = DateTimeWithZone),
          NamedToken(name = "date_time_offset", type = DateTimeWithOffset),
          NamedToken(name = "date_time", type = DateTime),
          NamedToken(name = "date_id", type = DateTimeWithZone),
          NamedToken(name = "date_offset", type = DateTimeWithOffset),
          NamedToken(name = "date", type = Date),
          NamedToken(name = "time_id", type = DateTimeWithZone),
          NamedToken(name = "time_offset", type = TimeWithOffset),
          NamedToken(name = "time", type = Time),
          NamedToken(name = "id", type = DateTimeWithZone),
          NamedToken(name = "offset", type = Offset),
          NamedToken(name = "no_arg", type = NoArg),
        )
    }
  }

  @Test
  fun tokenizeResourceWithInvalidIcuFormat() {
    "Test {{test}}"
      .assertNoTokensWithError("""Bad argument syntax: [at pattern index 6] "{test}}"""")
  }

  private fun String.assertTokens(vararg tokens: Token) {
    assertThat(
        tokenizeResource(
          StringResource(name = ResourceName("test"), description = "Test Description", text = this)
        )
      )
      .isEqualTo(
        TokenizedResource(
          name = ResourceName("test"),
          description = "Test Description",
          tokens = tokens.toList(),
          parsingError = null,
        )
      )
  }

  private fun String.assertNoTokensWithError(message: String) {
    assertThat(
        tokenizeResource(
          StringResource(name = ResourceName("test"), description = "Test Description", text = this)
        )
      )
      .isEqualTo(
        TokenizedResource(
          name = ResourceName("test"),
          description = "Test Description",
          tokens = emptyList(),
          parsingError = message,
        )
      )
  }
}
