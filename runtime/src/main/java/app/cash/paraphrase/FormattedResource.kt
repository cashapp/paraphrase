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

import android.icu.text.MessageFormat
import androidx.annotation.StringRes
import dev.drewhamilton.poko.ArrayContentBased
import dev.drewhamilton.poko.ArrayContentSupport
import dev.drewhamilton.poko.Poko

/**
 * A [FormattedResource] consists of:
 * 1. An Android string resource ID
 * 2. The arguments required to resolve it
 *
 * For example, if the following string was declared in the strings.xml resource file:
 * ```xml
 * <string name="detective_has_suspects">
 *   {suspects, plural,
 *     =0 {{detective} has no suspects}
 *     =1 {{detective} has one suspect}
 *     other {{detective} has # suspects}
 *   }
 * </string>
 * ```
 *
 * The [FormattedResource] would contain:
 * - The R.string.detective_has_suspects resource ID
 * - An integer value for the suspects argument
 * - A string value for the detective argument
 *
 * @property arguments Arguments passed directly to [MessageFormat.format].
 */
@OptIn(ArrayContentSupport::class)
@Poko
class FormattedResource(
  @StringRes val id: Int,
  @ArrayContentBased val arguments: Any,
)
