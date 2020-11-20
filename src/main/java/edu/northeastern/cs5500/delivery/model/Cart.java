package edu.northeastern.cs5500.delivery.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Cart {
    private List<MealQuantity> meals = new ArrayList<>();
}
