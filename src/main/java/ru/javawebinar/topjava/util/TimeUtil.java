package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TimeUtil {
    public final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    public final static DateTimeFormatter TIMEDATE_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss (dd MMM yyyy г.)");

    public static boolean isBetween(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) <= 0;
    }

    public static String formatTime(LocalTime time) {
        return TIME_FORMAT.format(time);
    }

    public static String formatDate(LocalDate date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTimeDate(LocalDateTime dateTime) {
        return TIMEDATE_FORMAT.format(dateTime);
    }
}
