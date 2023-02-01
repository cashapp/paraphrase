// Copyright Square, Inc.
package app.cash.paraphrase.plugin.model

/**
 * A raw string resource parsed from a strings.xml file.
 */
internal data class StringResource(
  val name: ResourceName,
  val description: String?,
  val text: String,
)
