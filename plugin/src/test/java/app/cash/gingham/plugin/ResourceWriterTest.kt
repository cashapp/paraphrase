// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.generator.writeResources
import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.Date

class ResourceWriterTest {
  @Test
  fun writeResourceWithNamedTokens() {
    TokenizedResource(
      name = "test_named",
      tokens = listOf(
        NamedToken(name = "first", type = Any::class),
        NamedToken(name = "second", type = Date::class),
        NamedToken(name = "third", type = Int::class),
        NamedToken(name = "fourth", type = Number::class),
        NamedToken(name = "fifth", type = String::class),
      )
    ).assertFile(
      """
      package com.gingham.test

      import app.cash.gingham.FormattedString
      import app.cash.gingham.FormattedStrings
      import app.cash.gingham.IcuNamedArgFormattedString
      import com.gingham.test.R
      import java.util.Date
      import kotlin.Any
      import kotlin.Int
      import kotlin.Number
      import kotlin.String

      public fun FormattedStrings.test_named(
        first: Any,
        second: Date,
        third: Int,
        fourth: Number,
        fifth: String,
      ): FormattedString {
        val namedArgs = mapOf("first" to first, "second" to second, "third" to third, "fourth" to fourth,
            "fifth" to fifth)
        return IcuNamedArgFormattedString(
          resourceId = R.string.test_named,
          namedArgs = namedArgs
        )
      }

      """.trimIndent()
    )
  }

  @Test
  fun writeResourceWithNumberedTokens() {
    TokenizedResource(
      name = "test_numbered",
      tokens = listOf(
        NumberedToken(number = 0, type = Any::class),
        NumberedToken(number = 1, type = Date::class),
        NumberedToken(number = 2, type = Int::class),
        NumberedToken(number = 3, type = Number::class),
        NumberedToken(number = 4, type = String::class),
      )
    ).assertFile(
      """
      package com.gingham.test

      import app.cash.gingham.FormattedString
      import app.cash.gingham.FormattedStrings
      import app.cash.gingham.IcuNumberedArgFormattedString
      import com.gingham.test.R
      import java.util.Date
      import kotlin.Any
      import kotlin.Int
      import kotlin.Number
      import kotlin.String

      public fun FormattedStrings.test_numbered(
        arg0: Any,
        arg1: Date,
        arg2: Int,
        arg3: Number,
        arg4: String,
      ): FormattedString {
        val numberedArgs = listOf(arg0, arg1, arg2, arg3, arg4)
        return IcuNumberedArgFormattedString(
          resourceId = R.string.test_numbered,
          numberedArgs = numberedArgs
        )
      }

      """.trimIndent()
    )
  }

  private fun TokenizedResource.assertFile(expected: String) {
    assertThat(
      buildString {
        writeResources(
          packageName = "com.gingham.test",
          tokenizedResources = listOf(this@assertFile)
        ).writeTo(this)
      }
    ).isEqualTo(expected)
  }
}
