// Copyright Square, Inc.
package app.cash.gingham.model

import androidx.annotation.StringRes

/**
 * A [FormattedResource] that uses numbered parameters to fill in an ICU message pattern.
 */
data class IcuNumberedArgFormattedResource(
  @StringRes override val id: Int,
  val numberedArgs: List<Any>
) : FormattedResource
