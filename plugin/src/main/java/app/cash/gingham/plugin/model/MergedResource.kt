// Copyright Square, Inc.
package app.cash.gingham.plugin.model

import kotlin.reflect.KClass

/**
 * A string resource parsed from a strings.xml file with its associated argument tokens.
 */
internal data class MergedResource(
  val name: ResourceName,
  val description: String?,
  val visibility: Visibility,
  val arguments: List<Argument>,
  /* True when the arguments bind to a contiguous set of integer tokens counting from 0. */
  val hasContiguousNumberedTokens: Boolean,
  val parsingErrors: List<String>,
) {
  data class Argument(
    /** The key into the format argument map. */
    val key: String,
    /** The public name used as a function parameter. */
    val name: String,
    val type: KClass<*>,
  )

  enum class Visibility {
    Private,
    Public,
  }
}
