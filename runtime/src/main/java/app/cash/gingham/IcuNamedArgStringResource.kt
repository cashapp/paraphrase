package app.cash.gingham

import android.content.res.Resources
import android.icu.text.MessageFormat
import androidx.annotation.StringRes

/**
 * A [FormattedStringResource] that uses named parameters to fill in an ICU message pattern.
 */
data class IcuNamedArgStringResource(
  @StringRes val resourceId: Int,
  val namedArgs: Map<String, Any>
) : FormattedStringResource {
  override fun resolve(resources: Resources): String {
    return MessageFormat.format(resources.getString(resourceId), namedArgs)
  }
}