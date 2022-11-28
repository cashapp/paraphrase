// Copyright Square, Inc.
package app.cash.gingham

import androidx.annotation.StringRes

/**
 * A [FormattedResource] that uses named parameters to fill in an ICU message pattern.
 */
data class IcuNamedArgFormattedResource(
  @StringRes override val id: Int,
  val namedArgs: Map<String, Any>
) : FormattedResource
