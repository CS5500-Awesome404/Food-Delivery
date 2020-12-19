package edu.northeastern.cs5500.delivery.controller;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.delivery.model.*;
import edu.northeastern.cs5500.delivery.model.Order.Status;
import edu.northeastern.cs5500.delivery.repository.InMemoryRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Provider;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserControllerTest {
    UserController userController;
    OrderController orderController;
    User user1;
    Meal meal1;
    Order order1;
    Cart cart1;

    @Before
    public void setUp() {
        userController =
                new UserController(
                        new InMemoryRepository<>(),
                        new Provider<OrderController>() {
                            @Override
                            public OrderController get() {
                                return orderController;
                            }
                        });
        orderController = new OrderController(new InMemoryRepository<>(), () -> userController);
        order1 = Order.builder().id(ObjectId.get()).status(Status.CREATED).total("9.80").build();
        List<Order> theOrders = new ArrayList<>();
        theOrders.add(order1);
        meal1 =
                Meal.builder()
                        .mealId(ObjectId.get())
                        .mealName("Fried rice")
                        .mealPrice(7.90)
                        .build();
        Meal meal2 = new Meal();
        meal2.setMealName("Donut");
        meal2.setMealPrice(9.9);
        Meal meal3 = new Meal();
        meal3.setMealName("Burger");
        meal3.setMealPrice(8.99);
        MealQuantity mealQuantity1 = new MealQuantity();
        mealQuantity1.setMeal(meal2);
        mealQuantity1.setQuantity(1);
        MealQuantity mealQuantity2 = new MealQuantity();
        mealQuantity2.setMeal(meal3);
        mealQuantity2.setQuantity(2);
        cart1 = new Cart();
        cart1.getMeals().add(mealQuantity1);
        cart1.getMeals().add(mealQuantity2);
        user1 =
                User.builder()
                        .id(ObjectId.get())
                        .name("John")
                        .address("1st ave n")
                        .email("abc@neu.edu")
                        .password("1234567")
                        .orders(theOrders)
                        .mealCart(cart1)
                        .build();
    }

    @Test
    public void testAddMealToCart() {
        User u = userController.addNewMealToCart(user1, meal1);
        Assert.assertEquals(u.getMealCart(), user1.getMealCart());
    }

    @Test
    public void testAddNewUser() throws Exception {
        Collection<User> users = userController.getUsers();
        Assert.assertEquals(users.size(), 1);
        userController.addNewUser(user1);
        Assert.assertEquals(users.size(), 2);
    }

    @Test
    public void testUpdateUser() throws Exception {
        user1.setEmail("abc@uw.edu");
        userController.updateUser(user1);
        Assert.assertEquals(user1.getEmail(), "abc@uw.edu");
    }

    @Test
    public void testDeleteUser() throws Exception {
        Collection<User> users = userController.getUsers();
        userController.addNewUser(user1);
        Assert.assertEquals(users.size(), 2);
        userController.deleteUser(user1.getId());
        Assert.assertEquals(users.size(), 1);
    }

    @Test
    public void testGetUser() throws Exception {
        Collection<User> users = userController.getUsers();
        userController.addNewUser(user1);
        User u = userController.getUser(user1.getId());
        Assert.assertEquals(u, user1);
    }

    @Test
    public void testGetUsers() {
        Collection<User> users = userController.getUsers();
        Assert.assertEquals(users.size(), 1);
    }

    @Test
    public void testFindUserWithName() throws Exception {
        Assert.assertNull(userController.findUserWithName("wang"));
    }
}
