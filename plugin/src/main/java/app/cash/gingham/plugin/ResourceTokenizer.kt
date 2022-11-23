// Copyright Square, Inc.
package app.cash.gingham.plugin

import app.cash.gingham.plugin.model.StringResource
import app.cash.gingham.plugin.model.TokenizedResource
import app.cash.gingham.plugin.model.TokenizedResource.Token
import app.cash.gingham.plugin.model.TokenizedResource.Token.NamedToken
import app.cash.gingham.plugin.model.TokenizedResource.Token.NumberedToken
import app.cash.icu.asIcuTokens
import app.cash.icu.tokens.Argument
import app.cash.icu.tokens.ChoiceArgument
import app.cash.icu.tokens.DateArgument
import app.cash.icu.tokens.IcuToken
import app.cash.icu.tokens.NumberArgument
import app.cash.icu.tokens.PluralArgument
import app.cash.icu.tokens.SelectArgument
import app.cash.icu.tokens.SelectOrdinalArgument
import app.cash.icu.tokens.TextArgument
import app.cash.icu.tokens.TimeArgument
import java.util.Date
import kotlin.reflect.KClass

internal fun tokenizeResource(stringResource: StringResource): TokenizedResource? {
  val icuTokens = try {
    stringResource.text.asIcuTokens()
  } catch (t: Throwable) {
    return null
  }

  val ginghamTokens = buildMap { addGinghamTokens(icuTokens) }
  if (ginghamTokens.isEmpty()) {
    return null
  }

  return TokenizedResource(
    name = stringResource.name,
    tokens = ginghamTokens.values.toList()
  )
}

private fun MutableMap<String, Token>.addGinghamTokens(icuTokens: List<IcuToken>) =
  icuTokens.forEach { addGinghamTokens(it) }

private fun MutableMap<String, Token>.addGinghamTokens(icuToken: IcuToken) {
  if (icuToken !is Argument) {
    return
  }

  if (!containsKey(icuToken.name)) {
    put(icuToken.name, icuToken.toGinghamToken())
  }

  if (icuToken is ChoiceArgument<*>) {
    icuToken.choices.forEach { addGinghamTokens(it.value) }
  }
}

private fun Argument.toGinghamToken(): Token =
  if (name.toIntOrNull() == null) {
    NamedToken(name = name, type = toType())
  } else {
    NumberedToken(number = name.toInt(), type = toType())
  }

private fun Argument.toType(): KClass<*> =
  when (this) {
    is DateArgument -> Date::class
    is NumberArgument -> Number::class
    is PluralArgument -> Int::class
    is SelectArgument -> String::class
    is SelectOrdinalArgument -> Int::class
    is TextArgument -> Any::class
    is TimeArgument -> Date::class
  }
