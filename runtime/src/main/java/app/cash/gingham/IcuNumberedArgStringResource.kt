package app.cash.gingham

import android.content.res.Resources
import android.icu.text.MessageFormat
import androidx.annotation.StringRes

/**
 * A [FormattedStringResource] that uses numbered parameters to fill in an ICU message pattern.
 */
data class IcuNumberedArgStringResource(
  @StringRes val resourceId: Int,
  val numberedArgs: List<Any>
) : FormattedStringResource {
  override fun resolve(resources: Resources): String {
    return MessageFormat.format(resources.getString(resourceId), *numberedArgs.toTypedArray())
  }
}