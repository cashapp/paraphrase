// Copyright Square, Inc.
package app.cash.gingham

import android.content.Context
import android.content.res.Resources
import android.icu.text.MessageFormat
import app.cash.gingham.model.FormattedResource
import app.cash.gingham.model.IcuNamedArgFormattedResource
import app.cash.gingham.model.IcuNumberedArgFormattedResource

object FormattedResources

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
