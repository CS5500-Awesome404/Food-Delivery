package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Meal {
    private String mealName;
    private Double mealPrice;

    public Meal() {}

    /** @return true if this delivery is valid */
    @JsonIgnore
    public boolean isValid() {
        return mealName != null && !mealName.isEmpty() && mealPrice >= 0;
    }
}
