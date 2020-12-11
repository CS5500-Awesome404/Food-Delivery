package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.*;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class UserController {

    private final GenericRepository<User> users;
    private final Provider<OrderController> orderControllerProvider;

    @Inject
    public UserController(
            GenericRepository<User> userRepository,
            Provider<OrderController> orderControllerProvider) {
        users = userRepository;
        this.orderControllerProvider = orderControllerProvider;
        log.info("UserController > construct");

        User user1 =
                User.builder()
                        .id(new ObjectId("5fcef7aa948d565fdd71a26c"))
                        .name("Claire")
                        .email("abcxyz@xyz.com")
                        .address("Seattle, WA")
                        .password("abc@@@")
                        .mealCart(new Cart())
                        .build();
        try {
            addNewUser(user1);
        } catch (AlreadyExistsException | BadRequestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // clean up the cart
    public Order convertCartToOrder(User user) throws Exception {
        // 1. create new order
        OrderController orderController = orderControllerProvider.get();
        Order newOrder = orderController.createNewOrder(user);

        // 3. clean up user's cart
        user.setMealCart(new Cart());
        users.update(user);

        return newOrder;
    }

    // Reminder: revise the quantity of same meals in UI
    public User addNewMealToCart(User user, Meal meal) {
        Cart userCart = user.getMealCart();
        Optional<MealQuantity> existingMeal =
                userCart.getMeals().stream()
                        .filter(mealQuantity -> mealQuantity.getMeal().equals(meal))
                        .findAny();

        if (existingMeal.isPresent()) {
            existingMeal.get().setQuantity(existingMeal.get().getQuantity() + 1);
        } else {
            MealQuantity newMeal = new MealQuantity();
            newMeal.setMeal(meal);
            newMeal.setQuantity(1);
            userCart.getMeals().add(newMeal);
        }

        // 2. return User for getting all added meals
        return users.update(user);
    }

    @Nonnull
    public User removeMealFromCart(@Nonnull User user, @Nonnull Meal meal) throws Exception {
        // 1. get the user and its cart
        Cart userCart = user.getMealCart();
        Optional<MealQuantity> existingMeal =
                userCart.getMeals().stream()
                        .filter(mealQuantity -> mealQuantity.getMeal().getMealId().equals(meal.getMealId()))
                        .findAny();

        if (existingMeal.isPresent()) {
            Integer newQuantity = existingMeal.get().getQuantity() - 1;
            if (newQuantity == 0) {
                userCart.getMeals().remove(existingMeal.get());
            } else {
                existingMeal.get().setQuantity(newQuantity);
            }
        } else {
            throw new BadRequestException();
        }
        return users.update(user);
    }

    public User addNewUser(User user) throws Exception {
        log.debug("UserController > createNewUser");
        if (!user.isValid()) {
            throw new BadRequestException();
        }
        ObjectId id = user.getId();
        if (id != null && users.get(id) != null) {
            throw new AlreadyExistsException();
        }
        return users.add(user);
    }

    public void updateUser(@Nonnull User user) {
        log.debug("UserController > updateUser");
        users.update(user);
    }

    public void deleteUser(@Nonnull ObjectId userId) throws BadRequestException {
        log.debug("UserController > deleteUser");
        users.delete(userId);
    }

    @Nullable
    public User getUser(@Nonnull ObjectId uuid) {
        log.debug("UserController > getUser({})", uuid);
        return users.get(uuid);
    }

    @Nullable
    public Collection<User> getUsers() {
        log.debug("UserController > getUsers");
        return users.getAll();
    }
}
