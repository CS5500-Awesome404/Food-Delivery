package edu.northeastern.cs5500.delivery.model;

import org.bson.types.ObjectId;

public interface Model {
    ObjectId getId();

    void setId(ObjectId id);
}
