package ru.javawebinar.topjava.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NamedQueries({
        @NamedQuery(name = Meal.GET, query = "select m from Meal m where m.id = :id and m.user.id=:userId"),
        @NamedQuery(name = Meal.DELETE, query = "delete from Meal m where m.id = :id and m.user.id=:userId"),
        @NamedQuery(name = Meal.ALL_SORTED, query = "select m from Meal m where m.user.id=:userId order by m.dateTime desc"),
        @NamedQuery(name = Meal.BETWEEN_SORTED, query = "select m from Meal m where m.user.id=:userId and m.dateTime between :startDate and :endDate order by m.dateTime desc")
})
@Entity
@Table(name = "meals", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date_time"}, name = "meals_unique_user_datetime_idx")})
public class Meal extends AbstractBaseEntity {

    public final static String GET = "Meal.get";
    public final static String DELETE = "Meal.delete";
    public final static String ALL_SORTED = "Meal.getAll";
    public final static String BETWEEN_SORTED = "Meal.getBetween";

    public final static int MIN_DESCRIPTION_LEN = 1;
    public final static int MAX_DESCRIPTION_LEN = 100;
    public final static int MIN_CALORIES_VALUE = 10;
    public final static int MAX_CALORIES_VALUE= 10000;

    @Column(name = "date_time", nullable = false, columnDefinition = "timestamp")
    @NotNull
    private LocalDateTime dateTime;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = MIN_DESCRIPTION_LEN, max = MAX_DESCRIPTION_LEN)
    private String description;

    @Column(name = "calories", nullable = false, columnDefinition = "int")
    @Range(min = MIN_CALORIES_VALUE, max = MAX_CALORIES_VALUE)
    private int calories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, columnDefinition = "int")
    @NotNull
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
