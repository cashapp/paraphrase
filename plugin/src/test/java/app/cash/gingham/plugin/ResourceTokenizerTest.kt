// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.StringResource
import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Date

class ResourceTokenizerTest {
  @Test
  fun tokenizeResourceWithNoArguments() {
    "Test".assertTokens()
  }

  @Test
  fun tokenizeResourceWithNamedSimpleTokens() {
    "Test {test} {test_number, number} {test_date, date} {test_time, time}".assertTokens(
      NamedToken(name = "test", type = Any::class),
      NamedToken(name = "test_number", type = Number::class),
      NamedToken(name = "test_date", type = Date::class),
      NamedToken(name = "test_time", type = Date::class),
    )
  }

  @Test
  fun tokenizeResourceWithNumberedSimpleTokens() {
    "Test {0} {1, number} {2, date} {3, time}".assertTokens(
      NumberedToken(number = 0, type = Any::class),
      NumberedToken(number = 1, type = Number::class),
      NumberedToken(number = 2, type = Date::class),
      NumberedToken(number = 3, type = Date::class),
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
        NamedToken(name = "test", type = Int::class),
        NamedToken(name = "test_nested", type = Any::class),
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
        NumberedToken(number = 0, type = Int::class),
        NumberedToken(number = 1, type = Any::class),
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
        NamedToken(name = "test", type = String::class),
        NamedToken(name = "test_nested", type = Any::class),
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
        NumberedToken(number = 0, type = String::class),
        NumberedToken(number = 1, type = Any::class),
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
        NamedToken(name = "test", type = Int::class),
        NamedToken(name = "test_nested", type = Any::class),
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
        NumberedToken(number = 0, type = Int::class),
        NumberedToken(number = 1, type = Any::class),
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
        NamedToken(name = "count", type = Int::class),
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
        NumberedToken(number = 0, type = Int::class),
      )
  }

  @Test
  fun tokenizeResourceWithInvalidIcuFormat() {
    "Test {{test}}".assertTokens()
  }

  private fun String.assertTokens(vararg tokens: Token) {
    assertThat(
      tokenizeResource(
        StringResource(
          name = "test",
          description = "Test Description",
          text = this
        )
      )
    ).isEqualTo(
      TokenizedResource(
        name = "test",
        description = "Test Description",
        tokens = tokens.toList()
      )
    )
  }
}
