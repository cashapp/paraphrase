// Copyright Square, Inc.
package com.squareup.cash.gingham.android


import android.content.Context
import android.content.res.Resources
import android.icu.text.MessageFormat
import com.squareup.cash.gingham.model.FormattedResource

/**
 * Resolves and returns the final formatted version of the given formatted string.
 */
fun Context.getString(formattedResource: FormattedResource): String =
  resources.getString(formattedResource)

/**
 * Resolves and returns the final formatted version of the given formatted string.
 */
fun Resources.getString(formattedResource: FormattedResource): String =
  MessageFormat.format(getString(formattedResource.id), formattedResource.arguments)
