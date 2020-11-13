package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;

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
