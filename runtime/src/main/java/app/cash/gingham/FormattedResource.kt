// Copyright Square, Inc.
package app.cash.gingham

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
 */
data class FormattedResource(@StringRes val id: Int, val arguments: Map<String, Any>)
