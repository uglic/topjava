package ru.javawebinar.topjava.web.convert;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class StringToLocalTime implements Formatter<LocalTime> {
    @Override
    public LocalTime parse(String source, Locale locale) throws ParseException {
        return DateTimeFormatter.ISO_LOCAL_TIME.parse(source, LocalTime::from);
    }

    @Override
    public String print(LocalTime localTime, Locale locale) {
        return localTime.toString();
    }
}
