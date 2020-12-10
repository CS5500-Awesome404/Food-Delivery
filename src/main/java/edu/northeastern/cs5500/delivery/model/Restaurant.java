package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Builder
@Data
public class Restaurant implements Model {
    private ObjectId id;
    // Ids of all meals provided by this restaurant.
    private List<Meal> menu;
    private String name;

    /** @return true if this order is valid */
    @JsonIgnore
    public boolean isValid() {

        return name != null && !name.isEmpty();
    }
}
