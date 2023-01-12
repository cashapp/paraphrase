// Copyright Square, Inc.
package com.squareup.cash.gingham.plugin

import com.squareup.cash.gingham.model.FormattedResource
import com.squareup.cash.gingham.plugin.model.TokenizedResource
import com.squareup.cash.gingham.plugin.model.TokenizedResource.Token
import com.squareup.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import com.squareup.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock

/**
 * Writes the given tokenized resources to a Kotlin source file.
 */
internal fun writeResources(
  packageName: String,
  tokenizedResources: List<TokenizedResource>
): FileSpec {
  val packageStringsType = ClassName(packageName = packageName, "R", "string")
  return FileSpec.builder(packageName = packageName, fileName = "FormattedResources")
    .addFileComment(
      """
        This code was generated by the Gingham Gradle plugin.
        Do not edit this file directly. Instead, edit the string resources in the source file.
      """.trimIndent()
    )
    .addImport(packageName = packageName, "R")
    .addType(
      TypeSpec.objectBuilder("FormattedResources")
        .apply {
          tokenizedResources.forEach { tokenizedResource ->
            addFunction(tokenizedResource.toFunSpec(packageStringsType))
          }
        }
        .build()
    )
    .build()
}

private fun TokenizedResource.toFunSpec(packageStringsType: TypeName): FunSpec {
  return FunSpec.builder(name)
    .apply { if (description != null) addKdoc(description) }
    .apply { tokens.forEach { addParameter(it.toParameterSpec()) } }
    .returns(FormattedResource::class.java)
    .apply {
      addStatement(
        "val arguments = mapOf(%L)",
        tokens.joinToString { "\"${it.argumentName}\" to ${it.parameterName}" }
      )
      addCode(
        buildCodeBlock {
          add("return %T(⇥\n", FormattedResource::class.java)
          addStatement("id = %T.%L,", packageStringsType, name)
          addStatement("arguments = arguments")
          add("⇤)\n")
        }
      )
    }
    .build()
}

private val Token.argumentName: String
  get() = when (this) {
    is NamedToken -> name
    is NumberedToken -> number.toString()
  }

private val Token.parameterName: String
  get() = when (this) {
    is NamedToken -> name
    is NumberedToken -> "arg$number"
  }

private fun Token.toParameterSpec(): ParameterSpec =
  ParameterSpec(
    name = parameterName,
    type = type.asClassName()
  )
