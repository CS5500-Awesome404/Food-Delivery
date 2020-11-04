package edu.northeastern.cs5500.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Courier implements Model {
    private ObjectId id;

    private String courierName;
    private String location;

    /** @return true if this delivery is valid */
    @JsonIgnore
    public boolean isValid() {

        return courierName != null && !courierName.isEmpty();
    }
}
