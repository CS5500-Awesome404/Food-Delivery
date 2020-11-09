package edu.northeastern.cs5500.delivery.model;

import lombok.Data;

@Data
public class MealQuantity {
    private Meal meal;
    private Integer quantity;
}
