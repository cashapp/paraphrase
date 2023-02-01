// Copyright Square, Inc.
package app.cash.paraphrase.sample.app

import app.cash.paraphrase.sample.app.test.FormattedResources

// Note: This is a compilation test, not a runtime test, so no assertions are needed.
class ParaphraseTest {
  fun testFormattedResources() {
    FormattedResources.app_test_text_argument("Jobu Tupaki")
  }
}
