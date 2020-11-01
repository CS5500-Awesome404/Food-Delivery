package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Meal implements Model {
    private ObjectId id;
    // The restaurant that serves this meal.
    private ObjectId restaurantId;
    private String mealName;
    private Double mealPrice;

    public Meal() {}

    /** @return true if this delivery is valid */
    @JsonIgnore
    public boolean isValid() {
        return mealName != null && !mealName.isEmpty() && mealPrice >= 0;
    }
}
