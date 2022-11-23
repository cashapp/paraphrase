// Copyright Square, Inc.
package app.cash.gingham.plugin.generator

import app.cash.gingham.plugin.model.TokenizedStringResource
import app.cash.icu.tokens.Argument
import app.cash.icu.tokens.DateArgument
import app.cash.icu.tokens.NumberArgument
import app.cash.icu.tokens.PluralArgument
import app.cash.icu.tokens.SelectArgument
import app.cash.icu.tokens.SelectOrdinalArgument
import app.cash.icu.tokens.TextArgument
import app.cash.icu.tokens.TimeArgument
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import java.util.Date

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
  tokenizedStringResources: List<TokenizedStringResource>
): FileSpec {
  val packageStringsType = ClassName(packageName = packageName, "R", "string")
  return FileSpec.builder(packageName = packageName, fileName = "FormattedStrings")
    .addImport(packageName = packageName, "R")
    .apply {
      tokenizedStringResources.forEach { tokenizedStringResource ->
        addFunction(tokenizedStringResource.asFunction(packageStringsType))
      }
    }
    .build()
}

private fun TokenizedStringResource.asFunction(packageStringsType: TypeName): FunSpec {
  val hasNumberedArgs = args.any { it.isNumbered() }
  val parameters = args.map { it.asParameter() }
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

private fun Argument.asParameter(): ParameterSpec =
  ParameterSpec(
    name = if (isNumbered()) "arg$name" else name,
    when (this) {
      is DateArgument -> Date::class
      is NumberArgument -> Number::class
      is PluralArgument -> Int::class
      is SelectArgument -> String::class
      is SelectOrdinalArgument -> Int::class
      is TextArgument -> String::class
      is TimeArgument -> Date::class
    }.asClassName()
  )

private fun Argument.isNumbered(): Boolean = name.toIntOrNull() != null
