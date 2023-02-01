// Copyright Square, Inc.
package app.cash.gingham.plugin.model

/**
 * A raw public resource parsed from e.g. a public.xml file.
 */
internal sealed interface PublicResource {
  /**
   * An actual public resource, referencing another declared resource by [name] and [type].
   */
  data class Named(
    val name: ResourceName,
    val type: String,
  ) : PublicResource

  /**
   * An empty <public /> declaration, typically used to ensure all of a library's resources are
   * private.
   */
  object EmptyDeclaration : PublicResource
}
