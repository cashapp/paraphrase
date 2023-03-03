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

import android.icu.util.ULocale
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.paraphrase.FormattedResource
import app.cash.paraphrase.getString
import app.cash.paraphrase.tests.LocalesTest.TestLocale.en_SA
import app.cash.paraphrase.tests.LocalesTest.TestLocale.en_US
import com.google.common.truth.Truth.assertThat
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class LocalesTest(
  @TestParameter private val testLocale: TestLocale,
) {
  @get:Rule val localeRule = LocaleAndTimeZoneRule(
    locale = testLocale.value,
  )

  private val context = InstrumentationRegistry.getInstrumentation().context
  private val releaseDate = LocalDate.of(2022, Month.MARCH, 24)

  /**
   * Must be instantiated after [localeRule] has taken effect, to be sure we're testing with a
   * Calendar that was created under the [testLocale].
   */
  private lateinit var resource: FormattedResource

  @Before fun instantiateResource() {
    resource = FormattedResources.locale_date(releaseDate)
  }

  @Test fun defaultLocale() {
    val expected = when (testLocale) {
      en_US -> "Mar 24, 2022"
      en_SA -> "Sha. 21, 1443 AH"
    }
    assertThat(context.getString(resource)).isEqualTo("A $expected B")
  }

  @Test fun franceLocale() {
    assertThat(context.getString(resource, Locale.FRANCE)).isEqualTo("A 24 mars 2022 B")
  }

  @Test fun germanyULocale() {
    assertThat(context.getString(resource, ULocale.GERMANY)).isEqualTo("A 24.03.2022 B")
  }

  @Test fun saudiArabiaLocale() {
    assertThat(context.getString(resource, Locale.forLanguageTag("en-SA")))
      .isEqualTo("A Sha. 21, 1443 AH B")
  }

  @Suppress("EnumEntryName", "unused")
  enum class TestLocale(
    val value: Locale,
  ) {
    en_US(Locale("en", "US")),
    en_SA(Locale("en", "SA")),
  }
}
