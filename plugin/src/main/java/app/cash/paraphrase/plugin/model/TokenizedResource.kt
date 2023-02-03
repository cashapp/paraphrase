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
