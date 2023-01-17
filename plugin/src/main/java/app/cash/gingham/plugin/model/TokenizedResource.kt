// Copyright Square, Inc.
package app.cash.gingham.plugin.model

import kotlin.reflect.KClass

/**
 * A string resource parsed from a strings.xml file with its associated argument tokens.
 */
internal data class TokenizedResource(
  val name: String,
  val description: String?,
  val tokens: List<Token>,
) {
  /* True when the tokens represent a contiguous set of integers counting from 0. */
  val hasContiguousNumberedTokens: Boolean =
    tokens.indices.toList() == tokens.map { (it as? Token.NumberedToken)?.number }

  sealed interface Token {
    val type: KClass<*>

    data class NamedToken(val name: String, override val type: KClass<*>) : Token
    data class NumberedToken(val number: Int, override val type: KClass<*>) : Token
  }
}
