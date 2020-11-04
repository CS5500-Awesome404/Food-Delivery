package edu.northeastern.cs5500.delivery.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class Cart {
    private Map<Meal, Integer> meals = new HashMap<>();
}
