// Copyright Square, Inc.
package app.cash.gingham.plugin.generator

import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import app.cash.icu.tokens.Argument
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock

private const val GINGHAM_PACKAGE = "app.cash.gingham"

private val FORMATTED_STRING =
  ClassName(packageName = GINGHAM_PACKAGE, "FormattedString")
private val FORMATTED_STRINGS =
  ClassName(packageName = GINGHAM_PACKAGE, "FormattedStrings")
private val ICU_NAMED_ARG_STRING_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "IcuNamedArgFormattedString")
private val ICU_NUMBERED_ARG_STRING_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "IcuNumberedArgFormattedString")

internal fun generateFormattedStringResources(
  packageName: String,
  tokenizedResources: List<TokenizedResource>
): FileSpec {
  val packageStringsType = ClassName(packageName = packageName, "R", "string")
  return FileSpec.builder(packageName = packageName, fileName = "FormattedStrings")
    .addImport(packageName = packageName, "R")
    .apply {
      tokenizedResources.forEach { tokenizedResource ->
        addFunction(tokenizedResource.asFunction(packageStringsType))
      }
    }
    .build()
}

private fun TokenizedResource.asFunction(packageStringsType: TypeName): FunSpec {
  val hasNumberedArgs = tokens.any { it is NumberedToken }
  val parameters = tokens.map { it.asParameter() }
  return FunSpec.builder(name)
    .receiver(FORMATTED_STRINGS)
    .apply { parameters.forEach { addParameter(it) } }
    .returns(FORMATTED_STRING)
    .apply {
      if (hasNumberedArgs) {
        addStatement("val numberedArgs = listOf(%L)", parameters.joinToString { it.name })
        addCode(
          buildCodeBlock {
            add("return %T(⇥\n", ICU_NUMBERED_ARG_STRING_RESOURCE)
            addStatement("resourceId = %T.%L,", packageStringsType, name)
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
            add("return %T(⇥\n", ICU_NAMED_ARG_STRING_RESOURCE)
            addStatement("resourceId = %T.%L,", packageStringsType, name)
            addStatement("namedArgs = namedArgs")
            add("⇤)\n")
          }
        )
      }
    }
    .build()
}

private fun Token.asParameter(): ParameterSpec =
  ParameterSpec(
    name = when (this) {
      is NamedToken -> name
      is NumberedToken -> "arg$number"
    },
    type = type.asClassName()
  )

private fun Argument.isNumbered(): Boolean = name.toIntOrNull() != null
