package ru.javawebinar.topjava.to;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class LocalDateTimeTo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDateTime dateTime;

    public LocalDateTimeTo() {
    }

    public LocalDateTimeTo(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "LocalDateTimeTo{" +
                "dateTime=" + dateTime +
                '}';
    }
}