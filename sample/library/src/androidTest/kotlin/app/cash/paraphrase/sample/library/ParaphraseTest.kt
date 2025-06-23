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
package app.cash.paraphrase.sample.library

import app.cash.paraphrase.sample.library.test.AndroidParaphraseResources

// Note: This is a compilation test, not a runtime test, so no assertions are needed.
class ParaphraseTest {
  fun testParaphraseResources() {
    AndroidParaphraseResources.library_test_text_argument("Jobu Tupaki")
  }
}
