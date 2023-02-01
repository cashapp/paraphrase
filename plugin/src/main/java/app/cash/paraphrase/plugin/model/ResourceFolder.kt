// Copyright Square, Inc.
package app.cash.paraphrase.plugin.model

/**
 * Represents "values-es" in `res/values-es/strings.xml`
 */
@JvmInline
internal value class ResourceFolder(val name: String) {
  companion object {
    val Default = ResourceFolder("values")
  }
}