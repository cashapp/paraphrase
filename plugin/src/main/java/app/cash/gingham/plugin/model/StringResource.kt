// Copyright Square, Inc.
package app.cash.gingham.plugin.model

/**
 * A string resource parsed from a strings.xml file.
 */
internal data class StringResource(val name: String, val description: String?, val text: String)
