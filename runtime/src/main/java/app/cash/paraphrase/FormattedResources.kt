// Copyright Square, Inc.
package app.cash.paraphrase

import android.content.Context
import android.content.res.Resources
import android.icu.text.MessageFormat

/**
 * Resolves and returns the final formatted version of the given formatted string.
 */
fun Context.getString(formattedResource: FormattedResource): String =
  resources.getString(formattedResource)

/**
 * Resolves and returns the final formatted version of the given formatted string.
 */
fun Resources.getString(formattedResource: FormattedResource): String =
  MessageFormat(getString(formattedResource.id)).format(formattedResource.arguments)
