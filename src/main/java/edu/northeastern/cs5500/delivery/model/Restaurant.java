package edu.northeastern.cs5500.delivery.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Restaurant {
    ObjectId id;
    // Ids of all meals provided by this restaurant.
    List<ObjectId> menu;
    String name;

    /** @return true if this order is valid */
    @JsonIgnore
    public boolean isValid() {
        return true;
    }
}
