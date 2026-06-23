/*
 * Copyright (C) 2026 Cash App
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
package app.cash.paraphrase.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class UnusedFormattedResourcesDetectorTest : LintDetectorTest() {
  override fun getDetector(): Detector = UnusedFormattedResourcesDetector()

  override fun getIssues(): List<Issue> = listOf(UnusedFormattedResourcesDetector.ISSUE)

  fun `test kotlin unreferenced reports issues`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          fun main() = Unit
          """,
        ),
      )
      .run()
      .expect(
        """
        res/values/strings.xml:3: Warning: The resource R.string.without_argument appears to be unused [UnusedFormattedResources]
                  <string name="without_argument">Without Argument</string>
                                ~~~~~~~~~~~~~~~~
        res/values/strings.xml:4: Warning: The resource R.string.with_argument appears to be unused [UnusedFormattedResources]
                  <string name="with_argument">With Argument: {argument}</string>
                                ~~~~~~~~~~~~~
        0 errors, 2 warnings
        """
      )
  }

  fun `test kotlin referenced via string resources is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          fun main() {
            val withoutArgument = R.string.without_argument
            val withArgument = R.string.with_argument
          }
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test kotlin referenced via formatted resources is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          fun main() {
            val withoutArgument = FormattedResources.without_argument
            val withArgument = FormattedResources.with_argument(argument = "argument")
          }
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test kotlin referenced via callable references is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          fun main() {
            val withoutArgument = FormattedResources::without_argument
            val withArgument = FormattedResources::with_argument
          }
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test kotlin referenced via imports is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          import com.test.FormattedResources.without_argument
          import com.test.FormattedResources.with_argument

          fun main() = Unit
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test kotlin referenced via test is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        kotlin(
          "src/test/java/com/test/Test.kt",
          """
          package com.test

          fun test() {
            val withoutArgument = R.string.without_argument
            val withArgument = FormattedResources.with_argument(argument = "argument")
          }
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test xml unreferenced reports issues`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        STRINGS_XML_ES,
        xml(
          "res/layout/main.xml",
          """
          <LinearLayout />
          """,
        ),
      )
      .run()
      .expect(
        """
        res/values/strings.xml:3: Warning: The resource R.string.without_argument appears to be unused [UnusedFormattedResources]
                  <string name="without_argument">Without Argument</string>
                                ~~~~~~~~~~~~~~~~
        res/values/strings.xml:4: Warning: The resource R.string.with_argument appears to be unused [UnusedFormattedResources]
                  <string name="with_argument">With Argument: {argument}</string>
                                ~~~~~~~~~~~~~
        0 errors, 2 warnings
        """
      )
  }

  fun `test xml referenced via attributes is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        xml(
          "res/layout/main.xml",
          """
          <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android">
            <TextView android:text="@string/without_argument" />
            <TextView android:text="@string/with_argument" />
          </LinearLayout>
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test xml referenced via aliases is clean`() {
    lint()
      .files(
        FORMATTED_RESOURCES,
        GENERATED_ANNOTATION,
        STRINGS_XML_EN,
        xml(
          "res/values/other_strings.xml",
          """
          <resources>
            <string name="without_argument_alias">@string/without_argument</string>
            <string name="with_argument_alias">@string/with_argument</string>
          </resources>
          """,
        ),
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          fun main() {
            val withoutArgument = R.string.without_argument_alias
            val withArgument = R.string.with_argument_alias
          }
          """,
        ),
      )
      .run()
      .expectClean()
  }

  fun `test xml referenced via manifest placeholder is clean`() {
    lint()
      .files(
        xml(
          "src/main/AndroidManifest.xml",
          $$"""
          <manifest xmlns:android="http://schemas.android.com/apk/res/android">
            <application android:label="${label}" />
          </manifest>
          """,
        ),
        xml(
          "src/main/res/values/strings.xml",
          """
          <resources>
            <string name="without_argument">Without Argument</string>
          </resources>
          """,
        ),
        kotlin(
          "src/main/java/com/test/Main.kt",
          """
          package com.test

          fun main() = Unit
          """,
        ),
        gradle(
          """
          android {
            defaultConfig {
              manifestPlaceholders = [label: "@string/without_argument"]
            }
          }
          """
        ),
      )
      .run()
      .expectClean()
  }

  companion object {
    private val FORMATTED_RESOURCES: TestFile =
      kotlin(
        "generated/source/paraphrase/com/test/FormattedResources.kt",
        """
        package com.test

        import androidx.annotation.StringRes
        import app.cash.paraphrase.FormattedResource
        import app.cash.paraphrase.Generated

        @Generated
        object FormattedResources {
          @get:StringRes
          public val without_argument: Int
            get() = R.string.without_argument

          public fun with_argument(argument: Any): FormattedResource =
            FormattedResource(id = R.string.with_argument, arguments = argument)
        }
        """,
      )
    private val GENERATED_ANNOTATION: TestFile =
      kotlin(
        "src/main/java/app/cash/paraphrase/Generated.kt",
        """
        package app.cash.paraphrase

        @Target(AnnotationTarget.CLASS)
        annotation class Generated
        """,
      )
    private val STRINGS_XML_EN: TestFile =
      xml(
        "res/values/strings.xml",
        """
        <resources>
          <string name="without_argument">Without Argument</string>
          <string name="with_argument">With Argument: {argument}</string>
        </resources>
        """,
      )
    private val STRINGS_XML_ES: TestFile =
      xml(
        "res/values-es/strings.xml",
        """
        <resources>
          <string name="without_argument">Without Argument</string>
          <string name="with_argument">With Argument: {argument}</string>
        </resources>
        """,
      )
  }
}
