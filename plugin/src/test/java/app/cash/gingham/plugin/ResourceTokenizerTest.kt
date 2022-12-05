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
    StringResource(name = "test", text = "Test").assertTokens()
  }

  @Test
  fun tokenizeResourceWithNamedSimpleTokens() {
    StringResource(
      name = "test",
      text = "Test {test} {test_number, number} {test_date, date} {test_time, time}"
    ).assertTokens(
      NamedToken(name = "test", type = Any::class),
      NamedToken(name = "test_number", type = Number::class),
      NamedToken(name = "test_date", type = Date::class),
      NamedToken(name = "test_time", type = Date::class),
    )
  }

  @Test
  fun tokenizeResourceWithNumberedSimpleTokens() {
    StringResource(
      name = "test",
      text = "Test {0} {1, number} {2, date} {3, time}"
    ).assertTokens(
      NumberedToken(number = 0, type = Any::class),
      NumberedToken(number = 1, type = Number::class),
      NumberedToken(number = 2, type = Date::class),
      NumberedToken(number = 3, type = Date::class),
    )
  }

  @Test
  fun tokenizeResourceWithNamedPluralArgument() {
    StringResource(
      name = "test",
      text = """
        {test, plural,
          zero {Test 0 {test_nested}}
          one {Test 1 {test_nested}}
          other {Test # {test_nested}}
        }
      """.trimIndent()
    ).assertTokens(
      NamedToken(name = "test", type = Int::class),
      NamedToken(name = "test_nested", type = Any::class),
    )
  }

  @Test
  fun tokenizeResourceWithNumberedPluralArgument() {
    StringResource(
      name = "test",
      text = """
        {0, plural,
          zero {Test 0 {1}}
          one {Test 1 {1}}
          other {Test # {1}}
        }
      """.trimIndent()
    ).assertTokens(
      NumberedToken(number = 0, type = Int::class),
      NumberedToken(number = 1, type = Any::class),
    )
  }

  @Test
  fun tokenizeResourceWithNamedSelectArgument() {
    StringResource(
      name = "test",
      text = """
        {test, select,
          red {Test red {test_nested}}
          green {Test green {test_nested}}
          blue {Test blue {test_nested}}
          other {Test other {test_nested}}
        }
      """.trimIndent()
    ).assertTokens(
      NamedToken(name = "test", type = String::class),
      NamedToken(name = "test_nested", type = Any::class),
    )
  }

  @Test
  fun tokenizeResourceWithNumberedSelectArgument() {
    StringResource(
      name = "test",
      text = """
        {0, select,
          red {Test red {1}}
          green {Test green {1}}
          blue {Test blue {1}}
          other {Test other {1}}
        }
      """.trimIndent()
    ).assertTokens(
      NumberedToken(number = 0, type = String::class),
      NumberedToken(number = 1, type = Any::class),
    )
  }

  @Test
  fun tokenizeResourceWithNamedSelectOrdinalArgument() {
    StringResource(
      name = "test",
      text = """
        {test, selectordinal,
          zero {Test 0 {test_nested}}
          one {Test 1 {test_nested}}
          other {Test # {test_nested}}
        }
      """.trimIndent()
    ).assertTokens(
      NamedToken(name = "test", type = Int::class),
      NamedToken(name = "test_nested", type = Any::class),
    )
  }

  @Test
  fun tokenizeResourceWithNumberedSelectOrdinalArgument() {
    StringResource(
      name = "test",
      text = """
        {0, selectordinal,
          zero {Test 0 {1}}
          one {Test 1 {1}}
          other {Test # {1}}
        }
      """.trimIndent()
    ).assertTokens(
      NumberedToken(number = 0, type = Int::class),
      NumberedToken(number = 1, type = Any::class),
    )
  }

  @Test
  fun tokenizeResourceWithReusedNamedChoiceArgument() {
    StringResource(
      name = "test",
      text = """
        {count, plural,
          zero {Test 0 {count}}
          one {Test 1 {count}}
          other {Test # {count}}
        }
      """.trimIndent()
    ).assertTokens(
      NamedToken(name = "count", type = Int::class),
    )
  }

  @Test
  fun tokenizeResourceWithReusedNumberedChoiceArgument() {
    StringResource(
      name = "test",
      text = """
        {0, plural,
          zero {Test 0 {0}}
          one {Test 1 {0}}
          other {Test # {0}}
        }
      """.trimIndent()
    ).assertTokens(
      NumberedToken(number = 0, type = Int::class),
    )
  }

  @Test
  fun tokenizeResourceWithInvalidIcuFormat() {
    StringResource(name = "test", text = "Test {{test}}").assertTokens()
  }

  private fun StringResource.assertTokens(vararg tokens: Token) {
    assertThat(tokenizeResource(this)).isEqualTo(
      TokenizedResource(name = name, tokens = tokens.toList())
    )
  }
}
