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
public class Order implements Model {
    public enum Status {
        CREATED,
        PREPARING,
        READY,
        DELIVERING,
        DELIVERED,
        CONFIRMED,
        CANCELLED;
    }

    ObjectId id;
    Status status;
    Cart orderContent;
    ObjectId courierId = null;
    String total = "0.00";

    /** @return true if this order is valid */
    @JsonIgnore
    public boolean isValid() {

        return orderContent != null;
    }
}
