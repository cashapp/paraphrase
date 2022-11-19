package app.cash.gingham.sample.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<TextView>(R.id.text).text = formatNamedArgs(
      sender = "Ashley",
      amount = "$50",
      recipient = "Briana",
    ).resolve(resources)
  }
}