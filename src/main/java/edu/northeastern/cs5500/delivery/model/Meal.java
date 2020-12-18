package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Meal {
    private String mealName;
    private ObjectId mealId;
    private Double mealPrice;
    private String pictureUrl;

    /** @return true if this delivery is valid */
    @JsonIgnore
    public boolean isValid() {
        return mealName != null && !mealName.isEmpty() && mealPrice >= 0;
    }
}
