package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Model {
    ObjectId id;
    String name;
    String email;
    String address;
    String password;

    // Temporary meals user has put into cart before placing order.
    Cart mealCart;
    List<Order> orders;

    /** @return true if this order is valid */
    @JsonIgnore
    public boolean isValid() {

        return name != null
                && !name.isEmpty()
                && email != null
                && !email.isEmpty()
                && address != null
                && !address.isEmpty()
                && password != null
                && !password.isEmpty();
    }
}
