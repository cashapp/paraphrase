// Copyright Square, Inc.
package app.cash.gingham.plugin

import org.w3c.dom.Node
import org.w3c.dom.NodeList

internal fun NodeList.asIterator(): Iterator<Node> = object : Iterator<Node> {
  private var index = 0
  override fun hasNext(): Boolean = index < length
  override fun next(): Node = item(index++)
}
