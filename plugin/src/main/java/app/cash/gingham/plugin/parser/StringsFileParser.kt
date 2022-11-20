package app.cash.gingham.plugin.parser

import app.cash.gingham.plugin.model.StringResource
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

// TODO: Filter out plurals, arrays, etc
fun parseStringResources(file: File): List<StringResource> =
  DocumentBuilderFactory.newInstance()
    .newDocumentBuilder()
    .parse(file)
    .documentElement
    .childNodes
    .asIterator()
    .asSequence()
    .filter { it.nodeType == Node.ELEMENT_NODE }
    .map {
      StringResource(
        name = it.attributes.getNamedItem("name").childNodes.item(0).textContent,
        text = it.textContent
      )
    }
    .toList()

private fun NodeList.asIterator(): Iterator<Node> = object : Iterator<Node> {
  private var index = 0
  override fun hasNext(): Boolean = index < length
  override fun next(): Node = item(index++)
}