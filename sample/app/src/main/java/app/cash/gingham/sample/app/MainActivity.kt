package app.cash.gingham.sample.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<TextView>(R.id.text).text = FormattedStrings.plural(
      amount = 1,
      sender = "Ashley",
      recipient = "Briana",
    ).resolve(resources)
  }
}