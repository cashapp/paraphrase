// Copyright Square, Inc.
package app.cash.gingham.tests

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.gingham.getString
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class TypesTest {
  private val context = InstrumentationRegistry.getInstrumentation().context
  private val releaseDate = Instant.ofEpochSecond(1648185825)

  @Test fun typeNone() {
    val formattedString = context.getString(FormattedResources.type_none("Z"))
    assertThat(formattedString).isEqualTo("A Z B")
    val formattedInteger = context.getString(FormattedResources.type_none(2))
    assertThat(formattedInteger).isEqualTo("A 2 B")
    val formattedDouble = context.getString(FormattedResources.type_none(2.345))
    assertThat(formattedDouble).isEqualTo("A 2.345 B")
    val formattedInstant = context.getString(FormattedResources.type_none(releaseDate))
    assertThat(formattedInstant).isEqualTo("A 2022-03-25T05:23:45Z B")
  }

  @Test fun typeNumber() {
    val formattedInteger = context.getString(FormattedResources.type_number(2))
    assertThat(formattedInteger).isEqualTo("A 2 B")
    val formattedDouble = context.getString(FormattedResources.type_number(2.345))
    assertThat(formattedDouble).isEqualTo("A 2.345 B")
  }

  @Test fun typeNumberInteger() {
    val formatted = context.getString(FormattedResources.type_number_integer(2))
    assertThat(formatted).isEqualTo("A 2 B")
  }

  @Test fun typeNumberCurrency() {
    val formatted = context.getString(FormattedResources.type_number_currency(2))
    assertThat(formatted).isEqualTo("A $2.00 B")
  }

  @Test fun typeNumberPercent() {
    val formatted = context.getString(FormattedResources.type_number_percent(.2))
    assertThat(formatted).isEqualTo("A 20% B")
  }

  @Test fun typeNumberCustom() {
    val formatted = context.getString(FormattedResources.type_number_custom(1234567))
    assertThat(formatted).isEqualTo("A 12,34,567 B")
  }

  @Test fun typeDate() {
    val formatted = context.getString(FormattedResources.type_date(releaseDate))
    assertThat(formatted).isEqualTo("A Mar 25, 2022 B")
  }

  @Test fun typeDateShort() {
    val formatted = context.getString(FormattedResources.type_date_short(releaseDate))
    assertThat(formatted).isEqualTo("A 3/25/22 B")
  }

  @Test fun typeDateMedium() {
    val formatted = context.getString(FormattedResources.type_date_medium(releaseDate))
    assertThat(formatted).isEqualTo("A Mar 25, 2022 B")
  }

  @Test fun typeDateLong() {
    val formatted = context.getString(FormattedResources.type_date_long(releaseDate))
    assertThat(formatted).isEqualTo("A March 25, 2022 B")
  }

  @Test fun typeDateFull() {
    val formatted = context.getString(FormattedResources.type_date_full(releaseDate))
    assertThat(formatted).isEqualTo("A Friday, March 25, 2022 B")
  }

  @Test fun typeDateCustom() {
    val formatted = context.getString(FormattedResources.type_date_custom(releaseDate))
    assertThat(formatted).isEqualTo("A 2022-03-25 B")
  }

  @Test fun typeTime() {
    val formatted = context.getString(FormattedResources.type_time(releaseDate))
    assertThat(formatted).isEqualTo("A 1:23:45 AM B")
  }

  @Test fun typeTimeShort() {
    val formatted = context.getString(FormattedResources.type_time_short(releaseDate))
    assertThat(formatted).isEqualTo("A 1:23 AM B")
  }

  @Test fun typeTimeMedium() {
    val formatted = context.getString(FormattedResources.type_time_medium(releaseDate))
    assertThat(formatted).isEqualTo("A 1:23:45 AM B")
  }

  @Test fun typeTimeLong() {
    val formatted = context.getString(FormattedResources.type_time_long(releaseDate))
    assertThat(formatted).isEqualTo("A 1:23:45 AM EDT B")
  }

  @Test fun typeTimeFull() {
    val formatted = context.getString(FormattedResources.type_time_full(releaseDate))
    assertThat(formatted).isEqualTo("A 1:23:45 AM Eastern Daylight Time B")
  }

  @Test fun typeTimeCustom() {
    val formatted = context.getString(FormattedResources.type_time_custom(releaseDate))
    assertThat(formatted).isEqualTo("A 01-23-45 B")
  }

  @Test fun typeDuration() {
    val formattedSeconds = context.getString(FormattedResources.type_duration(3.seconds))
    assertThat(formattedSeconds).isEqualTo("A 3 sec. B")
    val formattedMinutes = context.getString(FormattedResources.type_duration(3.minutes + 2.seconds))
    assertThat(formattedMinutes).isEqualTo("A 3:02 B")
    val formattedHours = context.getString(FormattedResources.type_duration(3.hours + 2.minutes + 1.seconds))
    assertThat(formattedHours).isEqualTo("A 3:02:01 B")
  }

  @Test fun typeOrdinal() {
    val formattedZero = context.getString(FormattedResources.type_ordinal(0))
    assertThat(formattedZero).isEqualTo("A 0th B")
    val formattedOne = context.getString(FormattedResources.type_ordinal(1))
    assertThat(formattedOne).isEqualTo("A 1st B")
    val formattedTwo = context.getString(FormattedResources.type_ordinal(2))
    assertThat(formattedTwo).isEqualTo("A 2nd B")
    val formattedThree = context.getString(FormattedResources.type_ordinal(3))
    assertThat(formattedThree).isEqualTo("A 3rd B")
    val formattedFour = context.getString(FormattedResources.type_ordinal(4))
    assertThat(formattedFour).isEqualTo("A 4th B")
  }

  @Test fun typeSpellout() {
    val formattedOnes = context.getString(FormattedResources.type_spellout(3))
    assertThat(formattedOnes).isEqualTo("A three B")
    val formattedTens = context.getString(FormattedResources.type_spellout(32))
    assertThat(formattedTens).isEqualTo("A thirty-two B")
    val formattedHundreds = context.getString(FormattedResources.type_spellout(321))
    assertThat(formattedHundreds).isEqualTo("A three hundred twenty-one B")
  }

  @Test fun typePlural() {
    val formatted0 = context.getString(FormattedResources.type_count_plural(0))
    assertThat(formatted0).isEqualTo("A Z B")
    val formatted1 = context.getString(FormattedResources.type_count_plural(1))
    assertThat(formatted1).isEqualTo("A Y B")
    val formatted2 = context.getString(FormattedResources.type_count_plural(2))
    assertThat(formatted2).isEqualTo("A X B")
  }

  @Test fun typeSelect() {
    val formattedAlpha = context.getString(FormattedResources.type_verse_select("alpha"))
    assertThat(formattedAlpha).isEqualTo("A Z B")
    val formattedBeta = context.getString(FormattedResources.type_verse_select("beta"))
    assertThat(formattedBeta).isEqualTo("A Y B")
    val formattedGamma = context.getString(FormattedResources.type_verse_select("gamma"))
    assertThat(formattedGamma).isEqualTo("A X B")
  }
}
