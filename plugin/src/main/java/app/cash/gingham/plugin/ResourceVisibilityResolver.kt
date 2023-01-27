// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.ResourceName

/**
 * Resolves the visibility (public or not) of resources.
 */
internal sealed interface ResourceVisibilityResolver {

  /**
   * True if [resource] is public, otherwise false.
   */
  fun isPublic(resource: ResourceName) : Boolean

  /**
   * Treats all resources as public. This is the default if no <public> declarations are made.
   */
  object EverythingIsPublic : ResourceVisibilityResolver {
    /**
     * Always true.
     */
    override fun isPublic(resource: ResourceName): Boolean = true
  }

  /**
   * Only resources in [allowlist] are public. This is the case if <public> is declared at least
   * once in a library.
   */
  class AllowlistIsPublic(
    private val allowlist: Collection<ResourceName>,
  ) : ResourceVisibilityResolver {
    /**
     * True if the allowlist contains [resource], otherwise false.
     */
    override fun isPublic(resource: ResourceName): Boolean {
      return allowlist.contains(resource)
    }
  }
}
