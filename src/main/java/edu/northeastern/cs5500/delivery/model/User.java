package edu.northeastern.cs5500.delivery.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class User implements Model {
    ObjectId id;
    String name;
    String email;
    String address;
    String password;

    // Temporary meals user has put into cart before placing order.
    List<ObjectId> mealCart;


    /** @return true if this order is valid */
    @JsonIgnore
    public boolean isValid() {
        return true;
    }
}
