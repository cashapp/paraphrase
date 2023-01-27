// Copyright Square, Inc.
package app.cash.gingham.tests

import java.util.Locale
import java.util.TimeZone
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class LocaleAndTimeZoneRule(
  private val locale: Locale = Locale.getDefault(),
  private val timeZone: TimeZone = TimeZone.getDefault(),
) : TestRule {
  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        val oldLocale = Locale.getDefault()
        Locale.setDefault(locale)

        val oldTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(timeZone)

        try {
          base.evaluate()
        } finally {
          Locale.setDefault(oldLocale)
          TimeZone.setDefault(oldTimeZone)
        }
      }
    }
  }
}
