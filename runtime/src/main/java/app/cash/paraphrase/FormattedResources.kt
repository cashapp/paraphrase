/*
 * Copyright (C) 2023 Cash App
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
package app.cash.paraphrase

import android.content.Context
import android.content.res.Resources
import android.icu.text.MessageFormat
import android.icu.util.ULocale
import java.util.Locale

/**
 * Resolves and returns the final formatted version of the given resource in the default locale.
 */
fun Context.getString(formattedResource: FormattedResource): String =
  resources.getString(formattedResource)

/**
 * Resolves and returns the final formatted version of the given resource in the given locale.
 */
fun Context.getString(formattedResource: FormattedResource, locale: Locale): String =
  resources.getString(formattedResource, locale)

/**
 * Resolves and returns the final formatted version of the given resource in the given locale.
 */
fun Context.getString(formattedResource: FormattedResource, locale: ULocale): String =
  resources.getString(formattedResource, locale)

/**
 * Resolves and returns the final formatted version of the given resource in the default locale.
 */
fun Resources.getString(formattedResource: FormattedResource): String =
  MessageFormat(getString(formattedResource.id)).format(formattedResource.arguments)

/**
 * Resolves and returns the final formatted version of the given resource in the given locale.
 */
fun Resources.getString(formattedResource: FormattedResource, locale: Locale): String =
  MessageFormat(getString(formattedResource.id), locale).format(formattedResource.arguments)

/**
 * Resolves and returns the final formatted version of the given resource in the given locale.
 */
fun Resources.getString(formattedResource: FormattedResource, locale: ULocale): String =
  MessageFormat(getString(formattedResource.id), locale).format(formattedResource.arguments)
