// Copyright Square, Inc.
package com.squareup.cash.gingham.model

import androidx.annotation.StringRes

/**
 * A [FormattedResource] that uses named arguments to fill in a message pattern.
 */
data class NamedArgFormattedResource(
  @StringRes override val id: Int,
  val arguments: Map<String, Any>
) : FormattedResource
