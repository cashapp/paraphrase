// Copyright Square, Inc.
package app.cash.gingham.plugin

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
      description = "Named Description",
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

      import app.cash.gingham.model.FormattedResource
      import app.cash.gingham.model.NamedArgFormattedResource
      import com.gingham.test.R
      import java.util.Date
      import kotlin.Any
      import kotlin.Int
      import kotlin.Number
      import kotlin.String

      public object FormattedResources {
        /**
         * Named Description
         */
        public fun test_named(
          first: Any,
          second: Date,
          third: Int,
          fourth: Number,
          fifth: String,
        ): FormattedResource {
          val arguments = mapOf("first" to first, "second" to second, "third" to third, "fourth" to
              fourth, "fifth" to fifth)
          return NamedArgFormattedResource(
            id = R.string.test_named,
            arguments = arguments
          )
        }
      }

      """.trimIndent()
    )
  }

  @Test
  fun writeResourceWithNumberedTokens() {
    TokenizedResource(
      name = "test_numbered",
      description = "Numbered Description",
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

      import app.cash.gingham.model.FormattedResource
      import app.cash.gingham.model.NumberedArgFormattedResource
      import com.gingham.test.R
      import java.util.Date
      import kotlin.Any
      import kotlin.Int
      import kotlin.Number
      import kotlin.String

      public object FormattedResources {
        /**
         * Numbered Description
         */
        public fun test_numbered(
          arg0: Any,
          arg1: Date,
          arg2: Int,
          arg3: Number,
          arg4: String,
        ): FormattedResource {
          val arguments = listOf(arg0, arg1, arg2, arg3, arg4)
          return NumberedArgFormattedResource(
            id = R.string.test_numbered,
            arguments = arguments
          )
        }
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
