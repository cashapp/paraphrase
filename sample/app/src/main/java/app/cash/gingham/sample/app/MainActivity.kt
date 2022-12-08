// Copyright Square, Inc.
package app.cash.gingham.sample.app

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
import app.cash.gingham.FormattedResource
import app.cash.gingham.FormattedResources
import app.cash.gingham.getString
import app.cash.gingham.sample.library.library_date_argument
import app.cash.gingham.sample.library.library_number_argument
import app.cash.gingham.sample.library.library_plural_argument
import app.cash.gingham.sample.library.library_select_argument
import app.cash.gingham.sample.library.library_select_ordinal_argument
import app.cash.gingham.sample.library.library_text_argument
import app.cash.gingham.sample.library.library_time_argument
import java.util.Date

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LazyColumn {
        item { Header(text = "App Strings") }
        items(APP_SAMPLES) { SampleRow(it) }

        item { Header(text = "Library Strings") }
        items(LIBRARY_SAMPLES) { SampleRow(it) }
      }
    }
  }

  @Composable
  private fun Header(text: String) {
    Text(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      text = text.uppercase(),
      fontSize = 14.sp,
      fontWeight = FontWeight.Black
    )
  }

  @Composable
  private fun SampleRow(sample: Sample) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        color = Color.DarkGray,
        text = sample.label,
        fontSize = 12.sp,
      )

      Text(
        text = resources.getString(sample.resource),
        fontSize = 16.sp,
      )
    }
  }

  companion object {
    data class Sample(val label: String, val resource: FormattedResource)

    private val APP_SAMPLES = listOf(
      Sample(
        label = "Text Argument",
        resource = FormattedResources.app_text_argument(name = "Jobu Tupaki")
      ),
      Sample(
        label = "Date Argument",
        resource = FormattedResources.app_date_argument(release_date = Date())
      ),
      Sample(
        label = "Number Argument",
        resource = FormattedResources.app_number_argument(budget = 10_000_000)
      ),
      Sample(
        label = "Time Argument",
        resource = FormattedResources.app_time_argument(showtime = Date())
      ),
      Sample(
        label = "Plural Argument",
        resource = FormattedResources.app_plural_argument(count = 5)
      ),
      Sample(
        label = "Select Argument",
        resource = FormattedResources.app_select_argument(verse = "alpha")
      ),
      Sample(
        label = "Select Ordinal Argument",
        resource = FormattedResources.app_select_ordinal_argument(count = 5)
      ),
    )

    private val LIBRARY_SAMPLES = listOf(
      Sample(
        label = "Text Argument",
        resource = FormattedResources.library_text_argument(name = "Jobu Tupaki")
      ),
      Sample(
        label = "Date Argument",
        resource = FormattedResources.library_date_argument(release_date = Date())
      ),
      Sample(
        label = "Number Argument",
        resource = FormattedResources.library_number_argument(budget = 10_000_000)
      ),
      Sample(
        label = "Time Argument",
        resource = FormattedResources.library_time_argument(showtime = Date())
      ),
      Sample(
        label = "Plural Argument",
        resource = FormattedResources.library_plural_argument(count = 5)
      ),
      Sample(
        label = "Select Argument",
        resource = FormattedResources.library_select_argument(verse = "alpha")
      ),
      Sample(
        label = "Select Ordinal Argument",
        resource = FormattedResources.library_select_ordinal_argument(count = 5)
      ),
    )
  }
}
