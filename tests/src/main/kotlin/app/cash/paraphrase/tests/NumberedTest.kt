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
package app.cash.paraphrase.tests

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.paraphrase.getString
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NumberedTest {
  private val context = InstrumentationRegistry.getInstrumentation().context
  private val paraphraseResources = AndroidParaphraseResources

  @Test fun numberedContiguousOne() {
    val formattedResource = paraphraseResources.numbered_contiguous_one("Z")
    assertThat(formattedResource.arguments as? Array<Any>)
      .asList()
      .containsExactly("Z")
      .inOrder()

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B")
  }

  @Test fun numberedContiguousThree() {
    val formattedResource = paraphraseResources.numbered_contiguous_three("Z", "Y", "X")
    assertThat(formattedResource.arguments as? Array<Any>)
      .asList()
      .containsExactly("Z", "Y", "X")
      .inOrder()

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B Y C X D")
  }

  @Test fun numberedSparseOne() {
    val formattedResource = paraphraseResources.numbered_sparse_one("Z")
    assertThat(formattedResource.arguments as? Map<String, Any>)
      .containsExactly("1", "Z")

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B")
  }

  @Test fun numberedSparseThree() {
    val formattedResource = paraphraseResources.numbered_sparse_three("Z", "Y", "X")
    assertThat(formattedResource.arguments as? Map<String, Any>)
      .containsExactly("1", "Z", "3", "Y", "5", "X")

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B Y C X D")
  }
}
