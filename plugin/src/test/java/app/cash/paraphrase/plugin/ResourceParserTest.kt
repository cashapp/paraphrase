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
package app.cash.paraphrase.plugin

import app.cash.paraphrase.plugin.model.ResourceName
import app.cash.paraphrase.plugin.model.StringResource
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResourceParserTest {
  @Test
  fun parseSingleStringResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <string name="test">Test</string>
      </resources>
    """.trimIndent().assertParse(
      StringResource(name = ResourceName("test"), description = null, text = "Test"),
    )
  }

  @Test
  fun parseMultipleStringResources() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <string name="test_1">Test 1</string>
        <string name="test_2">Test 2</string>
        <string name="test_3">Test 3</string>
      </resources>
    """.trimIndent().assertParse(
      StringResource(name = ResourceName("test_1"), description = null, text = "Test 1"),
      StringResource(name = ResourceName("test_2"), description = null, text = "Test 2"),
      StringResource(name = ResourceName("test_3"), description = null, text = "Test 3"),
    )
  }

  @Test
  fun parseStringResourceWithDescription() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <!-- Test Description -->
        <string name="test">Test</string>
      </resources>
    """.trimIndent().assertParse(
      StringResource(name = ResourceName("test"), description = "Test Description", text = "Test"),
    )
  }

  @Test
  fun parseUntranslatableStringResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <string name="test" translatable="false">Test</string>
      </resources>
    """.trimIndent().assertParse(
      StringResource(name = ResourceName("test"), description = null, text = "Test"),
    )
  }

  @Test
  fun ignorePluralResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <plurals name="test">
          <item quantity="zero">Test 0</item>
          <item quantity="one">Test 1</item>
          <item quantity="other">Test</item>
        </plurals>
      </resources>
    """.trimIndent().assertParse()
  }

  @Test
  fun ignoreStringArrayResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <string-array name="test">
          <item>Test 1</item>
          <item>Test 2</item>
          <item>Test 3</item>
        </string-array>
      </resources>
    """.trimIndent().assertParse()
  }

  private fun String.assertParse(vararg expectedResources: StringResource) {
    assertThat(parseResources(byteInputStream())).containsExactly(*expectedResources)
  }
}
