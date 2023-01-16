package app.cash.gingham.sample.app

import app.cash.gingham.sample.app.test.FormattedResources

// Note: This is a compilation test, not a runtime test, so no assertions are needed.
class GinghamTest {
  fun testFormattedResources() {
    FormattedResources.app_test_text_argument("Jobu Tupaki")
  }
}
