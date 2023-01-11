// Copyright Square, Inc.
package app.cash.gingham.model

import androidx.annotation.StringRes

/**
 * A [FormattedResource] that uses numbered arguments to fill in a message pattern.
 */
data class NumberedArgFormattedResource(
  @StringRes override val id: Int,
  val arguments: List<Any>
) : FormattedResource
