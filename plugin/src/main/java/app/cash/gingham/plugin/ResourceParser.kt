// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.RawResource
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Parses and returns all of the Android <string> resources declared in the given file.
 *
 * Ignores all other resources, including <plurals> and <string-array>.
 */
internal fun parseResources(file: File): List<RawResource> =
  parseResources(file.inputStream())

/**
 * Parses and returns all of the Android <string> resources declared in the given stream.
 *
 * Ignores all other resources, including <plurals> and <string-array>.
 */
internal fun parseResources(inputStream: InputStream): List<RawResource> =
  DocumentBuilderFactory.newInstance()
    .newDocumentBuilder()
    .parse(inputStream)
    .getElementsByTagName("string")
    .asIterator()
    .asSequence()
    .filter { it.nodeType == Node.ELEMENT_NODE }
    .map {
      RawResource(
        name = it.attributes.getNamedItem("name").childNodes.item(0).textContent,
        description = it.precedingComment()?.textContent?.trim(),
        text = it.textContent,
      )
    }
    .toList()

private fun Node.precedingComment(): Node? {
  val candidate = previousSibling ?: return null
  return when {
    candidate.nodeType == Node.COMMENT_NODE -> {
      candidate
    }
    candidate.nodeType == Node.TEXT_NODE && candidate.textContent.isBlank() -> {
      candidate.precedingComment()
    }
    else -> {
      null
    }
  }
}

private fun NodeList.asIterator(): Iterator<Node> = object : Iterator<Node> {
  private var index = 0
  override fun hasNext(): Boolean = index < length
  override fun next(): Node = item(index++)
}
