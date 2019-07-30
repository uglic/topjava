package ru.javawebinar.topjava.web.convert;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class StringToLocalDate implements Formatter<LocalDate> {
    @Override
    public LocalDate parse(String source, Locale locale) throws ParseException {
        return DateTimeFormatter.ISO_LOCAL_DATE.parse(source, LocalDate::from);
    }

    @Override
    public String print(LocalDate localDate, Locale locale) {
        return localDate.toString();
    }
}
