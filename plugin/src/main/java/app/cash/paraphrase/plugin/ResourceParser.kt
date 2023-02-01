// Copyright Square, Inc.
package app.cash.paraphrase.plugin

import app.cash.paraphrase.plugin.model.ResourceName
import app.cash.paraphrase.plugin.model.StringResource
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Node

/**
 * Parses and returns all of the Android <string> resources declared in the given file.
 *
 * Ignores all other resources, including <plurals> and <string-array>.
 */
internal fun parseResources(file: File): List<StringResource> =
  file.inputStream().use(::parseResources)

/**
 * Parses and returns all of the Android <string> resources declared in the given stream.
 *
 * Ignores all other resources, including <plurals> and <string-array>.
 */
internal fun parseResources(inputStream: InputStream): List<StringResource> =
  DocumentBuilderFactory.newInstance()
    .newDocumentBuilder()
    .parse(inputStream)
    .getElementsByTagName("string")
    .asIterator()
    .asSequence()
    .filter { it.nodeType == Node.ELEMENT_NODE }
    .map {
      val name = it.attributes.getNamedItem("name").childNodes.item(0).textContent
      StringResource(
        name = ResourceName(name),
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
