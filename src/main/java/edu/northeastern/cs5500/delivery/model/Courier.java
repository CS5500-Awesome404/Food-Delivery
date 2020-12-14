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
public class Courier implements Model {
    private ObjectId id;

    private String name;
    private String location;

    /** @return true if this delivery is valid */
    @JsonIgnore
    public boolean isValid() {

        return name != null && !name.isEmpty();
    }
}
