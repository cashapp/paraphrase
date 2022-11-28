// Copyright Square, Inc.
package app.cash.gingham

import android.content.Context
import android.content.res.Resources
import android.icu.text.MessageFormat

/**
 * A [FormattedResource] consists of:
 * 1. An Android string resource
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
 */
sealed interface FormattedResource {
  /**
   * The ID of the underlying Android string resource.
   */
  val id: Int
}

/**
 * Resolves and returns the final formatted version of the given formatted string.
 */
fun Context.getString(formattedResource: FormattedResource): String =
  resources.getString(formattedResource)

/**
 * Resolves and returns the final formatted version of the given formatted string.
 */
fun Resources.getString(formattedResource: FormattedResource): String =
  when (formattedResource) {
    is IcuNamedArgFormattedResource ->
      MessageFormat.format(
        getString(formattedResource.id),
        formattedResource.namedArgs
      )

    is IcuNumberedArgFormattedResource ->
      MessageFormat.format(
        getString(formattedResource.id),
        *formattedResource.numberedArgs.toTypedArray()
      )
  }
