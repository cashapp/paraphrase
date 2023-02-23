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
import app.cash.paraphrase.getString
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.Month
import java.util.Locale
import org.junit.Rule
import org.junit.Test

class LocalesTest {
  @get:Rule val localeRule = LocaleAndTimeZoneRule(
    locale = Locale("en", "US"),
  )

  private val context = InstrumentationRegistry.getInstrumentation().context
  private val releaseDate = LocalDate.of(2022, Month.MARCH, 24)

  @Test fun localeOverrides() {
    val resource = FormattedResources.locale_date(releaseDate)
    assertThat(context.getString(resource)).isEqualTo("A Mar 24, 2022 B")
    assertThat(context.getString(resource, Locale.FRENCH)).isEqualTo("A 24 mars 2022 B")
    assertThat(context.getString(resource, ULocale.GERMAN)).isEqualTo("A 24.03.2022 B")
  }
}
