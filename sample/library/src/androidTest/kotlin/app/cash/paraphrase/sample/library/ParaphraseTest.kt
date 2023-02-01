// Copyright Square, Inc.
package app.cash.paraphrase.sample.library

import app.cash.paraphrase.sample.library.test.FormattedResources

// Note: This is a compilation test, not a runtime test, so no assertions are needed.
class ParaphraseTest {
  fun testFormattedResources() {
    FormattedResources.library_test_text_argument("Jobu Tupaki")
  }
}
