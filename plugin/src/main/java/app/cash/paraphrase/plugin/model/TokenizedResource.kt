// Copyright Square, Inc.
package app.cash.paraphrase.plugin.model

import app.cash.paraphrase.plugin.TokenType

/**
 * A string resource parsed from a strings.xml file with its associated argument tokens.
 */
internal data class TokenizedResource(
  val name: ResourceName,
  val description: String?,
  val tokens: List<Token>,
  val parsingError: String?,
) {
  sealed interface Token {
    val type: TokenType

    data class NamedToken(val name: String, override val type: TokenType) : Token
    data class NumberedToken(val number: Int, override val type: TokenType) : Token
  }
}
