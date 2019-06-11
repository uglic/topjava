package ru.javawebinar.topjava.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TimeUtil {
    public final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    public static boolean isBetween(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) <= 0;
    }
}
