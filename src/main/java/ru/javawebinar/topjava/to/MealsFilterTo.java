package ru.javawebinar.topjava.to;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class MealsFilterTo implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;

    public MealsFilterTo() {
    }

    public MealsFilterTo(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
