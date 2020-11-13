package edu.northeastern.cs5500.delivery.controller;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.delivery.model.*;
import edu.northeastern.cs5500.delivery.repository.InMemoryRepository;
import javax.inject.Provider;
import org.junit.Before;
import org.junit.Test;

public class OrderControllerTest {

    OrderController orderController;
    UserController userController;
    User user1;
    Cart cart1;

    @Before
    public void setUp() {
        orderController =
                new OrderController(
                        new InMemoryRepository<>(),
                        new Provider<UserController>() {
                            @Override
                            public UserController get() {
                                return userController;
                            }
                        });
        userController = new UserController(new InMemoryRepository<>(), () -> orderController);

        Meal meal1 = new Meal();
        meal1.setMealName("Donut");
        meal1.setMealPrice(9.9);
        Meal meal2 = new Meal();
        meal2.setMealName("Burger");
        meal2.setMealPrice(8.99);

        MealQuantity mealQuantity1 = new MealQuantity();
        mealQuantity1.setMeal(meal1);
        mealQuantity1.setQuantity(1);
        MealQuantity mealQuantity2 = new MealQuantity();
        mealQuantity2.setMeal(meal2);
        mealQuantity2.setQuantity(2);

        Cart cart1 = new Cart();
        cart1.getMeals().add(mealQuantity1);
        cart1.getMeals().add(mealQuantity2);

        user1 = User.builder().build();

        user1.setMealCart(cart1);
    }

    @Test
    public void testOrderNotExist() {
        assertTrue(orderController.getOrders().isEmpty());
    }

    @Test
    public void testAddNewOrder() throws Exception {
        Order order = orderController.createNewOrder(user1);
        orderController.addNewOrder(order);
        assertFalse(orderController.getOrders().isEmpty());
    }

    @Test
    public void testCreateNewOrder() {
        Order order = orderController.createNewOrder(user1);
        assertEquals(order.getStatus(), Order.Status.CREATED);
        String total = "27.88";
        assertEquals(order.getTotal(), total);
    }

    @Test
    public void testCancelOneOrder() throws Exception {
        Order order = orderController.createNewOrder(user1);
        orderController.cancelOneOrder(order);
        assertEquals(order.getStatus(), Order.Status.CANCELLED);
    }

    @Test(expected = Exception.class)
    public void testUnableConfirmedOneOrder() throws Exception {
        Order order = orderController.createNewOrder(user1);
        orderController.confirmOneOrder(order);
        assertEquals(order.getStatus(), Order.Status.CONFIRMED);
    }
}
