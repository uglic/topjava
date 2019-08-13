package ru.javawebinar.topjava.to;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MealToOut extends BaseTo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String dateTime;
    private String description;
    private int calories;

    public MealToOut() {
    }

    public MealToOut(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = DATE_TIME_FORMATTER.format(dateTime);
        this.description = description;
        this.calories = calories;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public void setDateTime(String dateTimeTo) {
        this.dateTime = dateTimeTo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "MealTo{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}