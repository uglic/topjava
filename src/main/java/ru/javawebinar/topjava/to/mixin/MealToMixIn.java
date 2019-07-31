package ru.javawebinar.topjava.to.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public abstract class MealToMixIn {
    @JsonCreator
    MealToMixIn(@JsonProperty("id") Integer id,
                @JsonProperty("dateTime") LocalDateTime dateTime,
                @JsonProperty("description") String description,
                @JsonProperty("calories") int calories,
                @JsonProperty("excess") boolean excess) {
    }
}