// Copyright Square, Inc.
package app.cash.gingham.plugin.generator

import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock

private const val GINGHAM_PACKAGE = "app.cash.gingham"

private val FORMATTED_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "FormattedResource")
private val FORMATTED_RESOURCES =
  ClassName(packageName = GINGHAM_PACKAGE, "FormattedResources")
private val ICU_NAMED_ARG_FORMATTED_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "IcuNamedArgFormattedResource")
private val ICU_NUMBERED_ARG_FORMATTED_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "IcuNumberedArgFormattedResource")

/**
 * Writes the given tokenized resources to a Kotlin source file.
 */
internal fun writeResources(
  packageName: String,
  tokenizedResources: List<TokenizedResource>
): FileSpec {
  val packageStringsType = ClassName(packageName = packageName, "R", "string")
  return FileSpec.builder(packageName = packageName, fileName = "FormattedResources")
    .addImport(packageName = packageName, "R")
    .apply {
      tokenizedResources.forEach { tokenizedResource ->
        addFunction(tokenizedResource.toFunSpec(packageStringsType))
      }
    }
    .build()
}

private fun TokenizedResource.toFunSpec(packageStringsType: TypeName): FunSpec {
  val hasNumberedArgs = tokens.any { it is NumberedToken }
  val parameters = tokens.map { it.toParameterSpec() }
  return FunSpec.builder(name)
    .receiver(FORMATTED_RESOURCES)
    .apply { parameters.forEach { addParameter(it) } }
    .returns(FORMATTED_RESOURCE)
    .apply {
      if (hasNumberedArgs) {
        addStatement("val numberedArgs = listOf(%L)", parameters.joinToString { it.name })
        addCode(
          buildCodeBlock {
            add("return %T(⇥\n", ICU_NUMBERED_ARG_FORMATTED_RESOURCE)
            addStatement("id = %T.%L,", packageStringsType, name)
            addStatement("numberedArgs = numberedArgs")
            add("⇤)\n")
          }
        )
      } else {
        addStatement(
          "val namedArgs = mapOf(%L)",
          parameters.joinToString { "\"${it.name}\" to ${it.name}" }
        )
        addCode(
          buildCodeBlock {
            add("return %T(⇥\n", ICU_NAMED_ARG_FORMATTED_RESOURCE)
            addStatement("id = %T.%L,", packageStringsType, name)
            addStatement("namedArgs = namedArgs")
            add("⇤)\n")
          }
        )
      }
    }
    .build()
}

private fun Token.toParameterSpec(): ParameterSpec =
  ParameterSpec(
    name = when (this) {
      is NamedToken -> name
      is NumberedToken -> "arg$number"
    },
    type = type.asClassName()
  )
