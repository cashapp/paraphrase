/*
 * Copyright (C) 2023 Cash App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paraphrase.tests

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.paraphrase.getString
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.Rule
import org.junit.Test

class TypesTest {
  @get:Rule val localeRule = LocaleAndTimeZoneRule(
    locale = Locale("en", "US"),
  )

  private val context = InstrumentationRegistry.getInstrumentation().context
  private val releaseDate = LocalDate.of(2022, Month.MARCH, 24)
  private val releaseTime = LocalTime.of(19, 23, 45)
  private val releaseDateTime = ZonedDateTime.of(
    releaseDate,
    releaseTime,
    ZoneId.of("Pacific/Honolulu"),
  )

  @Test fun typeNone() {
    val formattedString = context.getString(FormattedResources.type_none("Z"))
    assertThat(formattedString).isEqualTo("A Z B")
    val formattedInteger = context.getString(FormattedResources.type_none(2))
    assertThat(formattedInteger).isEqualTo("A 2 B")
    val formattedDouble = context.getString(FormattedResources.type_none(2.345))
    assertThat(formattedDouble).isEqualTo("A 2.345 B")
    val formattedInstant =
      context.getString(FormattedResources.type_none(releaseDateTime.toInstant()))
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
    assertThat(formatted).isEqualTo("A Mar 24, 2022 B")
  }

  @Test fun typeDateShort() {
    val formatted = context.getString(FormattedResources.type_date_short(releaseDate))
    assertThat(formatted).isEqualTo("A 3/24/22 B")
  }

  @Test fun typeDateMedium() {
    val formatted = context.getString(FormattedResources.type_date_medium(releaseDate))
    assertThat(formatted).isEqualTo("A Mar 24, 2022 B")
  }

  @Test fun typeDateLong() {
    val formatted = context.getString(FormattedResources.type_date_long(releaseDate))
    assertThat(formatted).isEqualTo("A March 24, 2022 B")
  }

  @Test fun typeDateFull() {
    val formatted = context.getString(FormattedResources.type_date_full(releaseDate))
    assertThat(formatted).isEqualTo("A Thursday, March 24, 2022 B")
  }

  @Test fun typeDatePatternDateTimeZone() {
    val formatted =
      context.getString(FormattedResources.type_date_pattern_date_time_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A 3-24, 7PM HST B")
  }

  @Test fun typeDatePatternDateTimeOffset() {
    val formatted = context.getString(
      FormattedResources.type_date_pattern_date_time_offset(releaseDateTime.toOffsetDateTime()),
    )
    assertThat(formatted).isEqualTo("A 3-24, 7PM -10:00 B")
  }

  @Test fun typeDatePatternDateTime() {
    val localDateTime = releaseDateTime.toLocalDateTime()
    val formatted = context.getString(FormattedResources.type_date_pattern_date_time(localDateTime))
    assertThat(formatted).isEqualTo("A 3-24 7PM B")
  }

  @Test fun typeDatePatternDateZone() {
    val formatted =
      context.getString(FormattedResources.type_date_pattern_date_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A March (HST) B")
  }

  @Test fun typeDatePatternDateOffset() {
    val formatted = context.getString(
      FormattedResources.type_date_pattern_date_offset(releaseDateTime.toOffsetDateTime()),
    )
    assertThat(formatted).isEqualTo("A March (-10:00) B")
  }

  @Test fun typeDatePatternDate() {
    val formatted = context.getString(FormattedResources.type_date_pattern_date(releaseDate))
    assertThat(formatted).isEqualTo("A 2022-03-24 B")
  }

  @Test fun typeDatePatternTimeZone() {
    val formatted =
      context.getString(FormattedResources.type_date_pattern_time_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A 19:23 HST B")
  }

  @Test fun typeDatePatternTimeOffset() {
    val formatted = context.getString(
      FormattedResources.type_date_pattern_time_offset(
        // Ensures the UTC/GMT case works:
        releaseDateTime.withZoneSameLocal(ZoneOffset.UTC).toOffsetDateTime().toOffsetTime(),
      ),
    )
    assertThat(formatted).isEqualTo("A 19:23+0000 B")
  }

  @Test fun typeDatePatternTime() {
    val formatted = context.getString(FormattedResources.type_date_pattern_time(releaseTime))
    assertThat(formatted).isEqualTo("A 23 past 7 B")
  }

  @Test fun typeDatePatternZone() {
    val formatted = context.getString(FormattedResources.type_date_pattern_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A Hawaii-Aleutian Standard Time B")
  }

  @Test fun typeDatePatternOffset() {
    val formatted = context.getString(
      FormattedResources.type_date_pattern_offset(releaseDateTime.offset),
    )
    assertThat(formatted).isEqualTo("A GMT-10:00 B")
  }

  @Test fun typeDatePatternNone() {
    val formatted = context.getString(FormattedResources.type_date_pattern_none(null))
    assertThat(formatted).isEqualTo("A What is this for? B")
  }

  @Test fun typeTime() {
    val formatted = context.getString(FormattedResources.type_time(releaseTime))
    assertThat(formatted).isEqualTo("A 7:23:45 PM B")
  }

  @Test fun typeTimeShort() {
    val formatted = context.getString(FormattedResources.type_time_short(releaseTime))
    assertThat(formatted).isEqualTo("A 7:23 PM B")
  }

  @Test fun typeTimeMedium() {
    val formatted = context.getString(FormattedResources.type_time_medium(releaseTime))
    assertThat(formatted).isEqualTo("A 7:23:45 PM B")
  }

  @Test fun typeTimeLong() {
    val formatted = context.getString(FormattedResources.type_time_long(releaseDateTime))
    assertThat(formatted).isEqualTo("A 7:23:45 PM HST B")
  }

  @Test fun typeTimeFull() {
    val formatted = context.getString(FormattedResources.type_time_full(releaseDateTime))
    assertThat(formatted).isEqualTo("A 7:23:45 PM Hawaii-Aleutian Standard Time B")
  }

  @Test fun typeTimePatternDateTimeZone() {
    val formatted =
      context.getString(FormattedResources.type_time_pattern_date_time_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A 3-24, 7PM HST B")
  }

  @Test fun typeTimePatternDateTimeOffset() {
    val formatted = context.getString(
      FormattedResources.type_time_pattern_date_time_offset(releaseDateTime.toOffsetDateTime()),
    )
    assertThat(formatted).isEqualTo("A 3-24, 7PM -10 B")
  }

  @Test fun typeTimePatternDateTime() {
    val localDateTime = releaseDateTime.toLocalDateTime()
    val formatted = context.getString(FormattedResources.type_time_pattern_date_time(localDateTime))
    assertThat(formatted).isEqualTo("A 3-24 7PM B")
  }

  @Test fun typeTimePatternDateZone() {
    val formatted =
      context.getString(FormattedResources.type_time_pattern_date_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A March (HST) B")
  }

  @Test fun typeTimePatternDateOffset() {
    val formatted = context.getString(
      FormattedResources.type_time_pattern_date_offset(releaseDateTime.toOffsetDateTime()),
    )
    assertThat(formatted).isEqualTo("A March (-10) B")
  }

  @Test fun typeTimePatternDate() {
    val formatted = context.getString(FormattedResources.type_time_pattern_date(releaseDate))
    assertThat(formatted).isEqualTo("A 2022-03-24 B")
  }

  @Test fun typeTimePatternTimeZone() {
    val formatted =
      context.getString(FormattedResources.type_time_pattern_time_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A 19:23 HST B")
  }

  @Test fun typeTimePatternTimeOffset() {
    val formatted = context.getString(
      FormattedResources.type_time_pattern_time_offset(
        OffsetTime.of(releaseDateTime.toLocalTime(), releaseDateTime.offset),
      ),
    )
    assertThat(formatted).isEqualTo("A 19:23-1000 B")
  }

  @Test fun typeTimePatternTime() {
    val formatted = context.getString(FormattedResources.type_time_pattern_time(releaseTime))
    assertThat(formatted).isEqualTo("A 19-23-45 B")
  }

  @Test fun typeTimePatternZone() {
    val formatted = context.getString(FormattedResources.type_time_pattern_zone(releaseDateTime))
    assertThat(formatted).isEqualTo("A Hawaii-Aleutian Standard Time B")
  }

  @Test fun typeTimePatternOffset() {
    val formatted = context.getString(
      FormattedResources.type_time_pattern_offset(releaseDateTime.offset),
    )
    assertThat(formatted).isEqualTo("A GMT-10:00 B")
  }

  @Test fun typeTimePatternNone() {
    val formatted = context.getString(FormattedResources.type_time_pattern_none(null))
    assertThat(formatted).isEqualTo("A What is this for? B")
  }

  @Test fun typeTimeWithWinterTimeZone() {
    val winterDateTime = ZonedDateTime.of(
      LocalDate.of(2023, Month.FEBRUARY, 17),
      LocalTime.NOON,
      ZoneId.of("America/Chicago"),
    )
    val formatted = context.getString(FormattedResources.type_time_long(winterDateTime))
    assertThat(formatted).isEqualTo("A 12:00:00 PM CST B")
  }

  @Test fun typeTimeWithSummerTimeZone() {
    val summerDateTime = ZonedDateTime.of(
      LocalDate.of(2023, Month.JULY, 17),
      LocalTime.NOON,
      ZoneId.of("America/Chicago"),
    )
    val formatted = context.getString(FormattedResources.type_time_long(summerDateTime))
    assertThat(formatted).isEqualTo("A 12:00:00 PM CDT B")
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
    val zero = 0 // Requires an int overload to be invoked
    val formattedZero = context.getString(FormattedResources.type_ordinal(zero))
    assertThat(formattedZero).isEqualTo("A 0th B")
    val formattedOne = context.getString(FormattedResources.type_ordinal(1))
    assertThat(formattedOne).isEqualTo("A 1st B")
    val formattedTwo = context.getString(FormattedResources.type_ordinal(2))
    assertThat(formattedTwo).isEqualTo("A 2nd B")
    val formattedThree = context.getString(FormattedResources.type_ordinal(3))
    assertThat(formattedThree).isEqualTo("A 3rd B")
    val formattedFour = context.getString(FormattedResources.type_ordinal(4))
    assertThat(formattedFour).isEqualTo("A 4th B")
    val formattedLong = context.getString(FormattedResources.type_ordinal(Long.MAX_VALUE))
    val expected = if (Build.VERSION.SDK_INT >= 26) {
      "9,223,372,036,854,775,807th"
    } else {
      // ICU versions on older Android platforms lose bits by internally converting Long to Double:
      "9,223,372,036,854,776,000th"
    }
    assertThat(formattedLong).isEqualTo("A $expected B")
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
