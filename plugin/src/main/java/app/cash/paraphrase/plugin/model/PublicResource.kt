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

/** A raw public resource parsed from e.g. a public.xml file. */
internal sealed interface PublicResource {
  /** An actual public resource, referencing another declared resource by [name] and [type]. */
  data class Named(val name: ResourceName, val type: String) : PublicResource

  /**
   * An empty <public /> declaration, typically used to ensure all of a library's resources are
   * private.
   */
  object EmptyDeclaration : PublicResource
}
