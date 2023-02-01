// Copyright Square, Inc.
package app.cash.paraphrase

import android.icu.text.MessageFormat
import androidx.annotation.StringRes

/**
 * A [FormattedResource] consists of:
 * 1. An Android string resource ID
 * 2. The arguments required to resolve it
 *
 * For example, if the following string was declared in the strings.xml resource file:
 * ```xml
 * <string name="detective_has_suspects">
 *   {suspects, plural,
 *     =0 {{detective} has no suspects}
 *     =1 {{detective} has one suspect}
 *     other {{detective} has # suspects}
 *   }
 * </string>
 * ```
 *
 * The [FormattedResource] would contain:
 * - The R.string.detective_has_suspects resource ID
 * - An integer value for the suspects argument
 * - A string value for the detective argument
 *
 * @property arguments Arguments passed directly to [MessageFormat.format].
 */
class FormattedResource constructor(
  @StringRes val id: Int,
  val arguments: Any,
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is FormattedResource) return false

    return id == other.id &&
      arguments.flexibleEquals(other.arguments)
  }

  /**
   * Returns [Array.contentEquals] if this and [other] are both arrays, otherwise uses `==`.
   */
  private fun Any.flexibleEquals(other: Any?): Boolean {
    return this == other || (this is Array<*> && other is Array<*> && contentEquals(other))
  }

  override fun hashCode(): Int {
    var result = id
    result = 31 * result + arguments.flexibleHashCode()
    return result
  }

  /**
   * Returns [Array.contentHashCode] if this is an array, otherwise [hashCode].
   */
  private fun Any.flexibleHashCode(): Int {
    return if (this is Array<*>) contentHashCode() else hashCode()
  }

  override fun toString(): String {
    return "FormattedResource(id=$id, arguments=${arguments.flexibleToString()}"
  }

  /**
   * Returns [Array.contentToString] if this is an array, otherwise [toString].
   */
  private fun Any.flexibleToString(): String {
    return if (this is Array<*>) contentToString() else toString()
  }
}
