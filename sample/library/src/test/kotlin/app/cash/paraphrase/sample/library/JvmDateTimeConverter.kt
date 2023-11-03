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
package app.cash.paraphrase.sample.library

import app.cash.paraphrase.DateTimeConverter
import com.ibm.icu.util.Calendar
import com.ibm.icu.util.TimeZone
import com.ibm.icu.util.ULocale
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Converts `java.time` types used by Paraphrase to a [Calendar] that can be used by ICU to format.
 */
// TODO: Ship this in a new artifact?
object JvmDateTimeConverter : DateTimeConverter<Calendar> {

  private val Iso8601Locale = ULocale.Builder()
    .setExtension('u', "ca-iso8601")
    .build()

  override fun convertToCalendar(date: LocalDate): Calendar {
    return Calendar.getInstance(
      TimeZone.GMT_ZONE,
      Iso8601Locale,
    ).apply {
      set(date.year, date.monthValue - 1, date.dayOfMonth)
    }
  }

  override fun convertToCalendar(time: OffsetTime): Calendar {
    return Calendar.getInstance(
      TimeZone.getTimeZone("GMT${time.offset.id}"),
      Iso8601Locale,
    ).apply {
      set(Calendar.HOUR_OF_DAY, time.hour)
      set(Calendar.MINUTE, time.minute)
      set(Calendar.SECOND, time.second)
      set(Calendar.MILLISECOND, time.nano / 1_000_000)
    }
  }

  override fun convertToCalendar(time: LocalTime): Calendar {
    return Calendar.getInstance(
      TimeZone.GMT_ZONE,
      Iso8601Locale,
    ).apply {
      set(Calendar.HOUR_OF_DAY, time.hour)
      set(Calendar.MINUTE, time.minute)
      set(Calendar.SECOND, time.second)
      set(Calendar.MILLISECOND, time.nano / 1_000_000)
    }
  }

  override fun convertToCalendar(dateTime: ZonedDateTime): Calendar {
    return Calendar.getInstance(
      TimeZone.getTimeZone(dateTime.zone.id),
      Iso8601Locale,
    ).apply {
      set(
        dateTime.year,
        dateTime.monthValue - 1,
        dateTime.dayOfMonth,
        dateTime.hour,
        dateTime.minute,
        dateTime.second,
      )
      set(Calendar.MILLISECOND, dateTime.nano / 1_000_000)
    }
  }

  override fun convertToCalendar(dateTime: OffsetDateTime): Calendar {
    return Calendar.getInstance(
      TimeZone.getTimeZone("GMT${dateTime.offset.id}"),
      Iso8601Locale,
    ).apply {
      set(
        dateTime.year,
        dateTime.monthValue - 1,
        dateTime.dayOfMonth,
        dateTime.hour,
        dateTime.minute,
        dateTime.second,
      )
      set(Calendar.MILLISECOND, dateTime.nano / 1_000_000)
    }
  }

  override fun convertToCalendar(dateTime: LocalDateTime): Calendar {
    return Calendar.getInstance(
      TimeZone.GMT_ZONE,
      Iso8601Locale,
    ).apply {
      set(
        dateTime.year,
        dateTime.monthValue - 1,
        dateTime.dayOfMonth,
        dateTime.hour,
        dateTime.minute,
        dateTime.second,
      )
      set(Calendar.MILLISECOND, dateTime.nano / 1_000_000)
    }
  }

  override fun convertToCalendar(zoneOffset: ZoneOffset): Calendar {
    return Calendar.getInstance(
      TimeZone.getTimeZone("GMT${zoneOffset.id}"),
      Iso8601Locale,
    )
  }
}
