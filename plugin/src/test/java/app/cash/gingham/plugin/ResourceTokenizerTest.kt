// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.TokenType.Date
import app.cash.gingham.plugin.TokenType.None
import app.cash.gingham.plugin.TokenType.Number
import app.cash.gingham.plugin.TokenType.Plural
import app.cash.gingham.plugin.TokenType.Select
import app.cash.gingham.plugin.TokenType.SelectOrdinal
import app.cash.gingham.plugin.TokenType.Time
import app.cash.gingham.plugin.model.ResourceName
import app.cash.gingham.plugin.model.StringResource
import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResourceTokenizerTest {
  @Test
  fun tokenizeResourceWithNoArguments() {
    "Test".assertTokens()
  }

  @Test
  fun tokenizeResourceWithNamedSimpleTokens() {
    "Test {test} {test_number, number} {test_date, date} {test_time, time}".assertTokens(
      NamedToken(name = "test", type = None),
      NamedToken(name = "test_number", type = Number),
      NamedToken(name = "test_date", type = Date),
      NamedToken(name = "test_time", type = Time),
    )
  }

  @Test
  fun tokenizeResourceWithNumberedSimpleTokens() {
    "Test {0} {1, number} {2, date} {3, time}".assertTokens(
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
    """.trimIndent()
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
    """.trimIndent()
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
    """.trimIndent()
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
    """.trimIndent()
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
    """.trimIndent()
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
    """.trimIndent()
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
    """.trimIndent()
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
    """.trimIndent()
      .assertTokens(
        NumberedToken(number = 0, type = Plural),
        NumberedToken(number = 0, type = None),
        NumberedToken(number = 0, type = None),
        NumberedToken(number = 0, type = None),
      )
  }

  @Test
  fun tokenizeResourceWithInvalidIcuFormat() {
    "Test {{test}}".assertNoTokensWithError(
      """Bad argument syntax: [at pattern index 6] "{test}}"""",
    )
  }

  private fun String.assertTokens(
    vararg tokens: Token,
  ) {
    assertThat(
      tokenizeResource(
        StringResource(
          name = ResourceName("test"),
          description = "Test Description",
          text = this,
        ),
      ),
    ).isEqualTo(
      TokenizedResource(
        name = ResourceName("test"),
        description = "Test Description",
        tokens = tokens.toList(),
        parsingError = null,
      ),
    )
  }

  private fun String.assertNoTokensWithError(
    message: String,
  ) {
    assertThat(
      tokenizeResource(
        StringResource(
          name = ResourceName("test"),
          description = "Test Description",
          text = this,
        ),
      ),
    ).isEqualTo(
      TokenizedResource(
        name = ResourceName("test"),
        description = "Test Description",
        tokens = emptyList(),
        parsingError = message,
      ),
    )
  }
}
