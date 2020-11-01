package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Order implements Model {
    public enum Status {
        CREATED,
        PREPARING,
        READY,
        DELIVERING,
        DELIVERED;
    }

    ObjectId id;
    Status status;
    List<ObjectId> orderContent;
    ObjectId courierId = null;

    /** @return true if this order is valid */
    @JsonIgnore
    public boolean isValid() {
        return true;
    }
}
