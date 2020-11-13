package edu.northeastern.cs5500.delivery.model;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Cart {
    private List<MealQuantity> meals = new ArrayList<>();
}
