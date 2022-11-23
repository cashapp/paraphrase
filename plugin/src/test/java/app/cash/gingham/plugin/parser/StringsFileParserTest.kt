// Copyright Square, Inc.
package app.cash.gingham.plugin.parser

import app.cash.gingham.plugin.model.StringResource
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringsFileParserTest {
  @Test
  fun parseSingleStringResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <string name="test">Test</string>
      </resources>
    """.trimIndent().assertParse(
      StringResource(name = "test", text = "Test"),
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
      StringResource(name = "test_1", text = "Test 1"),
      StringResource(name = "test_2", text = "Test 2"),
      StringResource(name = "test_3", text = "Test 3"),
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
      StringResource(name = "test", text = "Test"),
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
      StringResource(name = "test", text = "Test"),
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
    assertThat(parseStringResources(byteInputStream())).containsExactly(*expectedResources)
  }
}
