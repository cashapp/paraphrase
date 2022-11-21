// Copyright Square, Inc.
package app.cash.gingham.plugin.model

import app.cash.icu.tokens.Argument

/**
 * A string resource parsed from a strings.xml file with its associated ICU tokens.
 */
data class TokenizedStringResource(val name: String, val args: Set<Argument>)
