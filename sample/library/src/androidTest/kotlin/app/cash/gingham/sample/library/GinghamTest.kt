package app.cash.gingham.sample.library

import app.cash.gingham.sample.library.test.FormattedResources

// Note: This is a compilation test, not a runtime test, so no assertions are needed.
class GinghamTest {
  fun testFormattedResources() {
    FormattedResources.library_test_text_argument("Jobu Tupaki")
  }
}
