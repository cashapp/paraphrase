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
package app.cash.paraphrase.plugin.model

import kotlin.reflect.KClass

/** A string resource parsed from a strings.xml file with its associated argument tokens. */
internal data class MergedResource(
  val name: ResourceName,
  val description: String?,
  val visibility: Visibility,
  val arguments: List<Argument>,
  val deprecation: Deprecation,
  /* True when the arguments bind to a contiguous set of integer tokens counting from 0. */
  val hasContiguousNumberedTokens: Boolean,
  val parsingErrors: List<String>,
) {
  data class Argument(
    /** The key into the format argument map. */
    val key: String,
    /** The public name used as a function parameter. */
    val name: String,
    val type: KClass<*>,
  )

  enum class Visibility {
    Private,
    Public,
  }

  sealed interface Deprecation {
    object None : Deprecation {
      override fun toString() = "None"
    }

    data class WithMessage(val message: String) : Deprecation
  }
}
