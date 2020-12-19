package edu.northeastern.cs5500.delivery.controller;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.*;
import edu.northeastern.cs5500.delivery.repository.InMemoryRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Provider;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestaurantControllerTest {
    RestaurantController restaurantController;
    OrderController orderController;
    UserController userController;
    Restaurant restaurant1;

    @Before
    public void setUp() {
        restaurantController =
                new RestaurantController(
                        new InMemoryRepository<>(),
                        new Provider<OrderController>() {
                            @Override
                            public OrderController get() {
                                return orderController;
                            }
                        });
        orderController = new OrderController(new InMemoryRepository<>(), () -> userController);
        userController = new UserController(new InMemoryRepository<>(), () -> orderController);
        List<Meal> meals = new ArrayList<>();
        meals.add(
                Meal.builder().mealId(ObjectId.get()).mealName("cupcake").mealPrice(1.20).build());
        restaurant1 =
                Restaurant.builder()
                        .id(ObjectId.get())
                        .name("Papa John's")
                        .address("1st ave")
                        .menu(meals)
                        .phone("2067875221")
                        .build();
    }

    @Test
    public void testAddRestaurant() throws Exception {
        Restaurant restaurant = restaurantController.addRestaurant(restaurant1);
        assertFalse(restaurantController.getRestaurants().isEmpty());
    }

    @Test
    public void testGetRestaurant() throws BadRequestException, AlreadyExistsException {
        restaurantController.addRestaurant(restaurant1);
        Restaurant r = restaurantController.getRestaurant(restaurant1.getId());
        Assert.assertEquals(r, restaurant1);
    }

    @Test
    public void testGetRestaurants() throws BadRequestException, AlreadyExistsException {
        restaurantController.addRestaurant(restaurant1);
        Collection<Restaurant> restaurants = restaurantController.getRestaurants();
        Assert.assertEquals(restaurants.size(), 4);
    }

    @Test
    public void testDeleteRestaurant() throws Exception {
        Collection<Restaurant> restaurants = restaurantController.getRestaurants();
        restaurantController.addRestaurant(restaurant1);
        Assert.assertEquals(restaurants.size(), 4);
        restaurantController.deleteRestaurant(restaurant1.getId());
        Assert.assertEquals(restaurants.size(), 3);
    }

    @Test
    public void testUpdateRestaurant() throws Exception {
        restaurantController.addRestaurant(restaurant1);
        Restaurant r = restaurantController.getRestaurant(restaurant1.getId());
        Assert.assertEquals(r.getName(), "Papa John's");
        restaurant1.setName("Papa Beard's");
        restaurantController.updateRestaurant(restaurant1);
        Assert.assertEquals(r.getName(), "Papa Beard's");
    }
}
