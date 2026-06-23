/*
 * Copyright (C) 2026 Cash App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paraphrase.lint

import app.cash.paraphrase.lint.UnusedFormattedResourcesDetector.Companion.ISSUE
import com.android.resources.ResourceType.STRING
import com.android.resources.ResourceUrl
import com.android.tools.lint.client.api.ResourceReference
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintMap
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.PartialResult
import com.android.tools.lint.detector.api.ResourceXmlDetector
import com.android.tools.lint.detector.api.Scope.ALL_JAVA_FILES
import com.android.tools.lint.detector.api.Scope.ALL_RESOURCE_FILES
import com.android.tools.lint.detector.api.Scope.MANIFEST
import com.android.tools.lint.detector.api.Scope.TEST_SOURCES
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.XmlContext
import com.android.utils.childrenIterator
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import java.io.File
import java.util.EnumSet
import org.jetbrains.kotlin.name.parentOrNull
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.java.asSafely
import org.w3c.dom.Attr
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Paraphrase breaks the standard Android UnusedResources lint in a couple ways:
 * - When `checkGeneratedSources` is disabled, lint reports false positives because it doesn't see
 *   resource references in the generated FormattedResources object.
 * - When `checkGeneratedSources` is enabled, lint reports false negatives because every resource is
 *   technically referenced in the generated FormattedResources object.
 *
 * This detector traces references through FormattedResources so that it can accurately determine
 * which string resources are used and which ones are not.
 */
internal class UnusedFormattedResourcesDetector : ResourceXmlDetector(), SourceCodeScanner {
  override fun getApplicableAttributes(): Collection<String> = ALL

  override fun getApplicableElements(): Collection<String> = ALL

  override fun getApplicableUastTypes(): List<Class<out UElement>> =
    listOf(
      UImportStatement::class.java,
      USimpleNameReferenceExpression::class.java,
      UCallExpression::class.java,
      UCallableReferenceExpression::class.java,
    )

  override fun createUastHandler(context: JavaContext): UElementHandler? =
    if (context.file.invariantSeparatorsPath.contains(other = "/generated/source/paraphrase/")) {
      // Ignore references from the generated FormattedResources file
      null
    } else {
      Handler(context)
    }

  override fun beforeCheckEachProject(context: Context) {
    // Record @string references
    context.project.buildVariant?.manifestPlaceholders?.values?.forEach { placeholder ->
      context.maybeRecordStringUrlReference(url = placeholder)
    }
  }

  override fun visitElement(context: XmlContext, element: Element) {
    // Record <string> declarations
    if (
      element.tagName == "string" &&
        element.parentNode.asSafely<Element>()?.tagName == "resources" &&
        context.file.parentFile?.name == "values" &&
        !context.isGeneratedResource()
    ) {
      val name = element.getAttributeNode("name")
      if (name != null && !name.value.isNullOrEmpty()) {
        context
          .getPartialResults(issue = ISSUE)
          .map()
          .getMapOrPutEmpty(key = KEY_STRING_DECLARATIONS)
          .put(key = name.value, value = context.getValueLocation(name))
      }
    }

    // Record @string references
    element
      .childrenIterator()
      .asSequence()
      .filter { child -> child.nodeType == Node.TEXT_NODE }
      .forEach { child -> context.maybeRecordStringUrlReference(url = child.nodeValue) }
  }

  override fun visitAttribute(context: XmlContext, attribute: Attr) {
    // Record @string references
    context.maybeRecordStringUrlReference(url = attribute.value)
  }

  override fun afterCheckRootProject(context: Context) {
    if (context.isGlobalAnalysis()) {
      checkPartialResults(
        context = context,
        partialResults = context.getPartialResults(issue = ISSUE),
      )
    }
  }

  override fun checkPartialResults(context: Context, partialResults: PartialResult) {
    // Aggregate the declarations and references
    val declarations = mutableMapOf<String, MutableList<Location>>()
    val references = mutableSetOf<String>()
    partialResults.maps().forEach { partialResult ->
      val partialDeclarations = partialResult.getMap(key = KEY_STRING_DECLARATIONS).orEmpty()
      partialDeclarations.forEach { name ->
        partialDeclarations.getLocation(key = name)?.let { location ->
          declarations.getOrPut(key = name) { mutableListOf() } += location
        }
      }

      partialResult.getMap(key = KEY_STRING_REFERENCES).orEmpty().forEach { partialReference ->
        references += partialReference
      }
    }

    // Report declarations without references
    declarations
      .filterNot { (name, _) -> references.contains(name) }
      .forEach { (name, locations) ->
        locations.forEach { location ->
          context.report(
            issue = ISSUE,
            location = location,
            message = "The resource `R.string.$name` appears to be unused",
          )
        }
      }
  }

  private class Handler(private val context: JavaContext) : UElementHandler() {
    override fun visitImportStatement(node: UImportStatement) {
      // Ignore imports with wildcards
      if (node.isOnDemand) {
        return
      }

      // Ignore imports that aren't from FormattedResources
      val importName = (node.sourcePsi as? KtImportDirective)?.importedFqName
      val parentName = importName?.parentOrNull()
      if (
        importName?.isRoot != false ||
          parentName?.isRoot != false ||
          parentName.shortName().asString() != "FormattedResources"
      ) {
        return
      }

      // Record indirect references to the string resource via Paraphrase
      val parentClass = context.evaluator.findClass(qualifiedName = parentName.asString())
      if (parentClass?.isFormattedResources() == true) {
        context.recordStringReference(name = importName.shortName().asString())
      }
    }

    override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
      // Record direct references to the string resource
      val resourceReference = ResourceReference.get(element = node)
      if (resourceReference != null && resourceReference.type == STRING) {
        context.recordStringReference(name = resourceReference.name)
      }

      // Record indirect references to the string resource via Paraphrase
      val resolvedNode = node.resolve() as? PsiMethod
      val resourceName = resolvedNode?.getStringResourceName()
      if (resolvedNode?.containingClass?.isFormattedResources() == true && resourceName != null) {
        context.recordStringReference(name = resourceName)
      } else {
        // Fall back to the receiver for cross-module references.
        val reference = node.uastParent as? UQualifiedReferenceExpression
        val receiver = (reference?.receiver as? UReferenceExpression)?.resolve() as? PsiClass
        if (reference?.selector == node && receiver?.isFormattedResources() == true) {
          context.recordStringReference(name = node.identifier)
        }
      }
    }

    override fun visitCallExpression(node: UCallExpression) {
      // Record indirect references to the string resource via Paraphrase
      val resolvedNode = node.resolve()
      val containingClass = resolvedNode?.containingClass
      val resourceName = resolvedNode?.getStringResourceName()
      if (containingClass?.isFormattedResources() == true && resourceName != null) {
        context.recordStringReference(name = resourceName)
      }
    }

    override fun visitCallableReferenceExpression(node: UCallableReferenceExpression) {
      // Record indirect references via a method/property reference
      val resolvedNode = node.resolve() as? PsiMethod
      val containingClass = resolvedNode?.containingClass
      val resourceName = resolvedNode?.getStringResourceName()
      if (containingClass?.isFormattedResources() == true && resourceName != null) {
        context.recordStringReference(name = resourceName)
      } else {
        // Fall back to the receiver for cross-module references.
        val receiver = (node.qualifierExpression as? UReferenceExpression)?.resolve() as? PsiClass
        if (receiver?.isFormattedResources() == true) {
          context.recordStringReference(name = node.callableName)
        }
      }
    }
  }

  companion object {
    @JvmField
    val ISSUE: Issue =
      Issue.create(
        id = "UnusedFormattedResources",
        briefDescription = "Unused string resource",
        explanation =
          """
          The string resource is declared in `strings.xml` but is not referenced anywhere — \
          neither directly via `R.string.<name>` nor indirectly via the Paraphrase-generated \
          `FormattedResources.<name>`.
          """,
        category = Category.PERFORMANCE,
        priority = 3,
        severity = Severity.WARNING,
        implementation =
          Implementation(
            UnusedFormattedResourcesDetector::class.java,
            EnumSet.of(ALL_JAVA_FILES, ALL_RESOURCE_FILES, MANIFEST, TEST_SOURCES),
          ),
      )
  }
}

private const val KEY_STRING_DECLARATIONS = "string_declarations"
private const val KEY_STRING_REFERENCES = "string_references"

/** Records a reference to the string resource if [url] is a `@string/` resource URL. */
private fun Context.maybeRecordStringUrlReference(url: String?) {
  val resourceUrl = url?.let(ResourceUrl::parse) ?: return
  if (!resourceUrl.isFramework && resourceUrl.type == STRING) {
    recordStringReference(name = resourceUrl.name)
  }
}

/** Records a reference to the string resource with the given [name]. */
private fun Context.recordStringReference(name: String) {
  getPartialResults(issue = ISSUE)
    .map()
    .getMapOrPutEmpty(key = KEY_STRING_REFERENCES)
    .put(key = name, value = true)
}

/**
 * Returns the value for the given key if the value is present and not null. Otherwise, inserts an
 * empty [LintMap] for the given key and returns that.
 */
private fun LintMap.getMapOrPutEmpty(key: String) = getMap(key) ?: LintMap().also { put(key, it) }

/** Returns the [LintMap] if it's not null, or an empty [LintMap] otherwise */
private fun LintMap?.orEmpty(): LintMap = this ?: LintMap()

/** True if this is a FormattedResources object generated by Paraphrase. */
private fun PsiClass.isFormattedResources(): Boolean =
  name == "FormattedResources" && hasAnnotation("app.cash.paraphrase.Generated")

/** The name of the string resource that was used to generate this method. */
private fun PsiMethod.getStringResourceName(): String {
  val name = name.substringBefore('$')
  return if (parameterList.parametersCount == 0) {
    name.removePrefix("get").replaceFirstChar { it.lowercase() }
  } else {
    name
  }
}

/** True if the current file is a generated resource. */
private fun XmlContext.isGeneratedResource(): Boolean {
  val filePath = file.absoluteFile.invariantSeparatorsPath
  return project.generatedResourceFolders.any { folder ->
    val absoluteFolder = if (folder.isAbsolute) folder else File(project.dir, folder.path)
    filePath.startsWith(absoluteFolder.invariantSeparatorsPath + "/")
  }
}
