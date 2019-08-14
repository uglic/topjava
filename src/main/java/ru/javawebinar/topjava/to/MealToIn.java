package ru.javawebinar.topjava.to;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

public class MealToIn extends BaseTo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDateTimeTo dateTime;

    @NotBlank
    @Size(min = 2, max = 120)
    private String description;

    @Range(min = 10, max = 5000)
    private int calories;

    public MealToIn() {
    }

    public MealToIn(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = new LocalDateTimeTo(dateTime);
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTimeTo getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public void setDateTime(LocalDateTimeTo dateTimeTo) {
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