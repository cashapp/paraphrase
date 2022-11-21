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
import app.cash.gingham.FormattedString
import app.cash.gingham.FormattedStrings
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
        items(SAMPLE_APP_STRINGS) { SampleString(it) }

        item { Header(text = "Library Strings") }
        items(SAMPLE_LIBRARY_STRINGS) { SampleString(it) }
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
  private fun SampleString(sampleString: SampleString) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        modifier = Modifier.padding(bottom = 4.dp),
        color = Color.DarkGray,
        text = sampleString.label,
        fontSize = 12.sp,
      )

      Text(
        text = resources.getString(sampleString.formattedString),
        fontSize = 16.sp,
      )
    }
  }

  companion object {
    data class SampleString(val label: String, val formattedString: FormattedString)

    private val SAMPLE_APP_STRINGS = listOf(
      SampleString(
        label = "Text Argument",
        formattedString = FormattedStrings.app_text_argument(name = "Jobu Tupaki")
      ),
      SampleString(
        label = "Date Argument",
        formattedString = FormattedStrings.app_date_argument(release_date = Date())
      ),
      SampleString(
        label = "Number Argument",
        formattedString = FormattedStrings.app_number_argument(budget = 10_000_000)
      ),
      SampleString(
        label = "Time Argument",
        formattedString = FormattedStrings.app_time_argument(showtime = Date())
      ),
      SampleString(
        label = "Plural Argument",
        formattedString = FormattedStrings.app_plural_argument(count = 5)
      ),
      SampleString(
        label = "Select Argument",
        formattedString = FormattedStrings.app_select_argument(verse = "alpha")
      ),
      SampleString(
        label = "Select Ordinal Argument",
        formattedString = FormattedStrings.app_select_ordinal_argument(count = 5)
      ),
    )

    private val SAMPLE_LIBRARY_STRINGS = listOf(
      SampleString(
        label = "Text Argument",
        formattedString = FormattedStrings.library_text_argument(name = "Jobu Tupaki")
      ),
      SampleString(
        label = "Date Argument",
        formattedString = FormattedStrings.library_date_argument(release_date = Date())
      ),
      SampleString(
        label = "Number Argument",
        formattedString = FormattedStrings.library_number_argument(budget = 10_000_000)
      ),
      SampleString(
        label = "Time Argument",
        formattedString = FormattedStrings.library_time_argument(showtime = Date())
      ),
      SampleString(
        label = "Plural Argument",
        formattedString = FormattedStrings.library_plural_argument(count = 5)
      ),
      SampleString(
        label = "Select Argument",
        formattedString = FormattedStrings.library_select_argument(verse = "alpha")
      ),
      SampleString(
        label = "Select Ordinal Argument",
        formattedString = FormattedStrings.library_select_ordinal_argument(count = 5)
      ),
    )
  }
}


