// Copyright Square, Inc.
package app.cash.gingham

import android.content.res.Resources
import android.icu.text.MessageFormat
import androidx.annotation.StringRes

/**
 * A [FormattedString] that uses numbered parameters to fill in an ICU message pattern.
 */
data class IcuNumberedArgFormattedString(
  @StringRes val resourceId: Int,
  val numberedArgs: List<Any>
) : FormattedString {
  override fun resolve(resources: Resources): String {
    return MessageFormat.format(resources.getString(resourceId), *numberedArgs.toTypedArray())
  }
}
