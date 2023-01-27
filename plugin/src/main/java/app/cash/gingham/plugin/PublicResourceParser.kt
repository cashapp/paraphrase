// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.PublicResource
import app.cash.gingham.plugin.model.ResourceName
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Node

/**
 * Parses and returns all of the Android <string> resources declared in the given file.
 *
 * Ignores all other resources, including <plurals> and <string-array>.
 */
internal fun parsePublicResources(file: File): List<PublicResource> =
  file.inputStream().use(::parsePublicResources)

/**
 * Parses and returns all of the Android <public> resources declared in the given stream, regardless
 * of type.
 */
internal fun parsePublicResources(inputStream: InputStream): List<PublicResource> =
  DocumentBuilderFactory.newInstance()
    .newDocumentBuilder()
    .parse(inputStream)
    .getElementsByTagName("public")
    .asIterator()
    .asSequence()
    .filter { it.nodeType == Node.ELEMENT_NODE }
    .map {
      val name = it.attributes.getNamedItem("name")?.childNodes?.item(0)?.textContent
      val type = it.attributes.getNamedItem("type")?.childNodes?.item(0)?.textContent

      when {
        name != null && type != null -> PublicResource.Named(
          name = ResourceName(name),
          type = type,
        )
        name == null && type == null -> PublicResource.EmptyDeclaration
        name == null -> throw IllegalArgumentException(
          "<public> resource with type $type must have a name",
        )
        else -> throw IllegalArgumentException(
          "<public> resource named $name has no type",
        )
      }
    }
    .toList()
