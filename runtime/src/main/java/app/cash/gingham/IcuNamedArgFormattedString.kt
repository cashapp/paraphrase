package app.cash.gingham

import android.content.res.Resources
import android.icu.text.MessageFormat
import androidx.annotation.StringRes

/**
 * A [FormattedString] that uses named parameters to fill in an ICU message pattern.
 */
data class IcuNamedArgFormattedString(
  @StringRes val resourceId: Int,
  val namedArgs: Map<String, Any>
) : FormattedString {
  override fun resolve(resources: Resources): String {
    return MessageFormat.format(resources.getString(resourceId), namedArgs)
  }
}