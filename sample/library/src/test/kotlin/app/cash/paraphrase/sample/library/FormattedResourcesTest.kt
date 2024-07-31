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

import androidx.annotation.StringRes
import com.google.common.truth.Truth.assertThat
import com.ibm.icu.text.MessageFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.util.Locale
import org.junit.Test

class FormattedResourcesTest {

  private val stringResolver = FakeStringResolver(
    R.string.library_date_argument to "{release_date, date, short}",
    R.string.library_time_argument to "{showtime, time, short}",
  )

  private val formattedResources = FormattedResources(JvmDateTimeConverter)

  @Test fun date() {
    val formattedResource =
      formattedResources.library_date_argument(LocalDate.of(2023, Month.NOVEMBER, 3))
    val result = MessageFormat(stringResolver.getString(formattedResource.id), Locale.US)
      .format(formattedResource.arguments)
    assertThat(result).isEqualTo("11/3/23")
  }

  @Test fun time() {
    val formattedResource =
      formattedResources.library_time_argument(LocalTime.of(14, 37, 21))
    val result = MessageFormat(stringResolver.getString(formattedResource.id), Locale.US)
      .format(formattedResource.arguments)
    assertThat(result).isEqualTo("2:37 PM")
  }

  private class FakeStringResolver(
    private val strings: Map<Int, String>,
  ) {

    constructor(vararg strings: Pair<Int, String>) : this(mapOf(*strings))

    fun getString(@StringRes id: Int): String = strings.getValue(id)
  }
}
