package ru.javawebinar.topjava.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    // to store dateTime as timestampZ we must know at which TimeZone LocalDateTime objects are.
    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.systemDefault();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // DataBase doesn't support LocalDate.MIN/MAX
    private static final LocalDate MIN_DATE = LocalDate.of(1, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(3000, 1, 1);

    public static LocalDateTime adjustStartDateTime(LocalDate localDate) {
        return adjustDateTime(localDate, MIN_DATE, LocalTime.MIN);
    }

    public static LocalDateTime adjustEndDateTime(LocalDate localDate) {
        return adjustDateTime(localDate, MAX_DATE, LocalTime.MAX);
    }

    private static LocalDateTime adjustDateTime(LocalDate localDate, LocalDate defaultDate, LocalTime adjustTime) {
        return LocalDateTime.of(localDate != null ? localDate : defaultDate, adjustTime);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalDate parseLocalDate(@Nullable String str) {
        return StringUtils.isEmpty(str) ? null : LocalDate.parse(str);
    }

    public static LocalTime parseLocalTime(@Nullable String str) {
        return StringUtils.isEmpty(str) ? null : LocalTime.parse(str);
    }

    public static java.sql.Timestamp adjustStartDateTimeToSqlTimestamp(LocalDateTime dateTime) {
        return java.sql.Timestamp.valueOf(dateTime != null ? dateTime : LocalDateTime.of(MIN_DATE, LocalTime.MIN));
    }

    public static java.sql.Timestamp adjustEndDateTimeToSqlTimestamp(LocalDateTime dateTime) {
        return java.sql.Timestamp.valueOf(dateTime != null ? dateTime : LocalDateTime.of(MAX_DATE, LocalTime.MAX));
    }

    public static java.sql.Timestamp getTimestampForZone(LocalDateTime localDateTime, ZoneId zoneIdOfDateTime) {
        return java.sql.Timestamp.from(ZonedDateTime.of(localDateTime, zoneIdOfDateTime).toInstant());
    }
}
