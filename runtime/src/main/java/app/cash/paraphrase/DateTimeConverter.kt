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
package app.cash.paraphrase

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Converts `java.time` types used by Paraphrase to a [Calendar] that can be used by ICU to format.
 *
 * [Calendar] is generic so the system-appropriate ICU calendar implementation can be used:
 * `android.icu.util` on Android, or `com.ibm.icu` on the JVM.
 *
 * This interface's public API may change.
 */
@SubclassOptInRequired(DateTimeConverter.SubclassOptIn::class)
public interface DateTimeConverter<out Calendar : Any> {

  /**
   * Converts [date] to a [Calendar] used by ICU to format.
   *
   * The resulting calendar's time fields are undefined and its time zone is GMT. These are ignored
   * by the formatter.
   */
  public fun convertToCalendar(date: LocalDate): Calendar

  /**
   * Converts [time] to a [Calendar] used by ICU to format.
   *
   * The resulting calendar's date fields are undefined. They are ignored by the formatter.
   */
  public fun convertToCalendar(time: OffsetTime): Calendar

  /**
   * Converts [time] to a [Calendar] used by ICU to format.
   *
   * The resulting calendar's date fields are undefined and its time zone is GMT. These are ignored
   * by the formatter.
   */
  public fun convertToCalendar(time: LocalTime): Calendar

  /**
   * Converts [dateTime] to a [Calendar] used by ICU to format.
   */
  public fun convertToCalendar(dateTime: ZonedDateTime): Calendar

  /**
   * Converts [dateTime] to a [Calendar] used by ICU to format.
   */
  public fun convertToCalendar(dateTime: OffsetDateTime): Calendar

  /**
   * Converts [dateTime] to a [Calendar] used by ICU to format.
   *
   * The resulting calendar's time zone is GMT. This is ignored by the formatter.
   */
  public fun convertToCalendar(dateTime: LocalDateTime): Calendar

  /**
   * Converts [zoneOffset] to a [Calendar] used by ICU to format.
   *
   * The resulting calendar's date and time fields are undefined. These are ignored by the
   * formatter.
   */
  public fun convertToCalendar(zoneOffset: ZoneOffset): Calendar

  /**
   * [DateTimeConverter] is not stable for public extension; its public API may change.
   */
  @RequiresOptIn
  public annotation class SubclassOptIn
}
