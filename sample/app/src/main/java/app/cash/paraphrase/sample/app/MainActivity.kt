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
package app.cash.paraphrase.sample.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paraphrase.FormattedResource
import app.cash.paraphrase.compose.formattedResource
import app.cash.paraphrase.sample.app.AndroidParaphraseResources as AppAndroidParaphraseResources
import app.cash.paraphrase.sample.app.ParaphraseResources as AppParaphraseResources
import app.cash.paraphrase.sample.library.AndroidParaphraseResources as LibraryAndroidParaphraseResources
import app.cash.paraphrase.sample.library.ParaphraseResources as LibraryParaphraseResources
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val appSamples = appSamples(AppAndroidParaphraseResources)
    val librarySamples = librarySamples(LibraryAndroidParaphraseResources)
    setContent {
      LazyColumn {
        item { Header(text = "App Strings") }
        items(appSamples) { SampleRow(it) }

        item { Header(text = "Library Strings") }
        items(librarySamples) { SampleRow(it) }
      }
    }
  }

  @Composable
  private fun Header(text: String) {
    Text(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      text = text.uppercase(),
      fontSize = 14.sp,
      fontWeight = FontWeight.Black,
    )
  }

  @Composable
  private fun SampleRow(sample: Sample) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        color = Color.DarkGray,
        text = sample.label,
        fontSize = 12.sp,
      )

      Text(
        text = formattedResource(sample.resource),
        fontSize = 16.sp,
      )
    }
  }

  companion object {
    data class Sample(val label: String, val resource: FormattedResource)

    private fun appSamples(
      paraphraseResources: AppParaphraseResources,
    ): List<Sample> = listOf(
      Sample(
        label = "Text Argument",
        resource = paraphraseResources.app_text_argument(name = "Jobu Tupaki"),
      ),
      Sample(
        label = "Date Argument",
        resource = paraphraseResources.app_date_argument(release_date = LocalDate.now()),
      ),
      Sample(
        label = "Number Argument",
        resource = paraphraseResources.app_number_argument(budget = 10_000_000),
      ),
      Sample(
        label = "Time Argument",
        resource = paraphraseResources.app_time_argument(showtime = ZonedDateTime.now()),
      ),
      Sample(
        label = "Plural Argument",
        resource = paraphraseResources.app_plural_argument(count = 5),
      ),
      Sample(
        label = "Select Argument",
        resource = paraphraseResources.app_select_argument(verse = "alpha"),
      ),
      Sample(
        label = "Select Ordinal Argument",
        resource = paraphraseResources.app_select_ordinal_argument(count = 5),
      ),
    )

    private fun librarySamples(
      paraphraseResources: LibraryParaphraseResources,
    ): List<Sample> = listOf(
      Sample(
        label = "Text Argument",
        resource = paraphraseResources.library_text_argument(name = "Jobu Tupaki"),
      ),
      Sample(
        label = "Date Argument",
        resource = paraphraseResources.library_date_argument(release_date = LocalDate.now()),
      ),
      Sample(
        label = "Number Argument",
        resource = paraphraseResources.library_number_argument(budget = 10_000_000),
      ),
      Sample(
        label = "Time Argument",
        resource = paraphraseResources.library_time_argument(showtime = LocalTime.now()),
      ),
      Sample(
        label = "Plural Argument",
        resource = paraphraseResources.library_plural_argument(count = 5),
      ),
      Sample(
        label = "Select Argument",
        resource = paraphraseResources.library_select_argument(verse = "alpha"),
      ),
      Sample(
        label = "Select Ordinal Argument",
        resource = paraphraseResources.library_select_ordinal_argument(count = 5),
      ),
      @Suppress("DEPRECATION")
      Sample(
        label = "Choice argument",
        resource = paraphraseResources.library_choice_argument(outlook = 100),
      ),
    )
  }
}
