package app.cash.gingham.plugin.generator

import app.cash.gingham.plugin.model.TokenizedStringResource
import app.cash.icu.tokens.Argument
import app.cash.icu.tokens.Date
import app.cash.icu.tokens.Number
import app.cash.icu.tokens.Plural
import app.cash.icu.tokens.Select
import app.cash.icu.tokens.SelectOrdinal
import app.cash.icu.tokens.Text
import app.cash.icu.tokens.Time
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import org.gradle.configurationcache.extensions.capitalized

private const val GINGHAM_PACKAGE = "app.cash.gingham"

private val ANDROID_RESOURCES =
  ClassName(packageName = "android.content.res", "Resources")
private val FORMATTED_STRING_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "FormattedStringResource")
private val ICU_NAMED_ARG_STRING_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "IcuNamedArgStringResource")
private val ICU_NUMBERED_ARG_STRING_RESOURCE =
  ClassName(packageName = GINGHAM_PACKAGE, "IcuNumberedArgStringResource")

fun generateFormattedStringResources(
  packageName: String,
  tokenizedStringResources: List<TokenizedStringResource>
): FileSpec {
  val packageStringsType = ClassName(packageName = packageName, "R", "string")
  return FileSpec.builder(packageName = packageName, fileName = "GinghamStringResources")
    .addImport(packageName = packageName, "R")
    .apply {
      tokenizedStringResources.forEach { tokenizedStringResource ->
        addFunction(tokenizedStringResource.asFormatFunction(packageStringsType))
        addFunction(tokenizedStringResource.asResolveFunction(packageStringsType))
      }
    }
    .build()
}

private fun TokenizedStringResource.asFormatFunction(packageStringsType: TypeName): FunSpec {
  val hasNumberedArgs = args.any { it.isNumbered() }
  val parameters = args.map { it.asParameter() }
  return FunSpec.builder("format${name.snakeCaseToUpperCamelCase()}")
    .receiver(packageStringsType)
    .apply { parameters.forEach { addParameter(it) } }
    .returns(FORMATTED_STRING_RESOURCE)
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

private fun TokenizedStringResource.asResolveFunction(packageStringsType: TypeName): FunSpec {
  val parameters = args.map { it.asParameter() }
  return FunSpec.builder("resolve${name.snakeCaseToUpperCamelCase()}")
    .receiver(packageStringsType)
    .addParameter(ParameterSpec(name = "resources", ANDROID_RESOURCES))
    .apply { parameters.forEach { addParameter(it) } }
    .returns(String::class)
    .apply {
      addCode(
        buildCodeBlock {
          add("val formattedStringResource = format%L(⇥\n", name.snakeCaseToUpperCamelCase())
          parameters.forEach { addStatement("%1L = %1L,", it.name) }
          add("⇤)\n")
        }
      )
      addStatement("return formattedStringResource.resolve(resources = resources)")
    }
    .build()
}

private fun Argument.asParameter(): ParameterSpec =
  ParameterSpec(
    name = if (isNumbered()) "arg$name" else name,
    when (this) {
      is Date -> java.util.Date::class
      is Number -> kotlin.Number::class
      is Plural -> Int::class
      is Select -> String::class
      is SelectOrdinal -> Int::class
      is Text -> String::class
      is Time -> java.util.Date::class
    }.asClassName()
  )

private fun Argument.isNumbered(): Boolean = name.toIntOrNull() != null

private fun String.snakeCaseToUpperCamelCase() =
  replace(regex = Regex("_[a-zA-Z]")) { it.value[1].uppercase() }.capitalized()
