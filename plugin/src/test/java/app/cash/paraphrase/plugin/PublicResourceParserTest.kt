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

import app.cash.paraphrase.plugin.model.PublicResource
import app.cash.paraphrase.plugin.model.ResourceName
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class PublicResourceParserTest {
  @Test
  fun parseSinglePublicResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <public name="test" type="string" />
      </resources>
    """.trimIndent().assertParse(
      PublicResource.Named(name = ResourceName("test"), type = "string"),
    )
  }

  @Test
  fun parseMultiplePublicResources() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <public name="test_1" type="string" />
        <public name="test_2" type="anim" />
        <public name="test_3" type="color" />
      </resources>
    """.trimIndent().assertParse(
      PublicResource.Named(name = ResourceName("test_1"), type = "string"),
      PublicResource.Named(name = ResourceName("test_2"), type = "anim"),
      PublicResource.Named(name = ResourceName("test_3"), type = "color"),
    )
  }

  @Test
  fun parseEmptyPublicResource() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <public />
      </resources>
    """.trimIndent().assertParse(
      PublicResource.EmptyDeclaration,
    )
  }

  @Test
  fun ignoreOtherResources() {
    """
      <?xml version="1.0" encoding="utf-8"?>
      <resources>
        <public name="test1" type="bool" />
        <bool name="test1">true</bool>
        <string name="test2">String test2</string>
      </resources>
    """.trimIndent().assertParse(
      PublicResource.Named(name = ResourceName("test1"), type = "bool"),
    )
  }

  @Test
  fun throwOnNamedResourceWithNoName() {
    assertThrows(IllegalArgumentException::class.java) {
      """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
          <public type="string" />
        </resources>
      """.trimIndent().assertParse()
    }
  }

  @Test
  fun throwOnNamedResourceWithNoType() {
    assertThrows(IllegalArgumentException::class.java) {
      """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
          <public name="test" />
        </resources>
      """.trimIndent().assertParse()
    }
  }

  private fun String.assertParse(vararg expectedResources: PublicResource) {
    assertThat(parsePublicResources(byteInputStream())).containsExactly(*expectedResources)
  }
}
