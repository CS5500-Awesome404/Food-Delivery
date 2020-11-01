package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.User;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

        User user1 = new User();
        user1.setName("Claire");
        user1.setEmail("abcxyz@xyz.com");
        user1.setAddress("Seattle, WA");
        user1.setPassword("abc@@@");
        try {
            addNewUser(user1);
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (BadRequestException e) {
            e.printStackTrace();
        }
    }

    // create new order
    // clean up the cart
    public Order createNewOrderFromCurrentCart(User user) throws Exception {
        // 1. create new order
        OrderController orderController = orderControllerProvider.get();
        Order newOrder = new Order();

        // 2. add the meals in userCart to the new generated order
        List<ObjectId> userCart = user.getMealCart();
        newOrder.setOrderContent(userCart);
        newOrder.setStatus(Order.Status.CREATED);

        // 3. clean up user's cart
        user.setMealCart(new ArrayList<>());
        users.update(user);

        return orderController.addNewOrder(newOrder);
    }

    // Reminder: revise the quantity of same meals in UI
    public User addNewMealToCart(ObjectId userId, ObjectId mealId) {
        // 1. get the user and its cart
        User user = users.get(userId);
        List<ObjectId> userCart = user.getMealCart();

        // 2. add meal to the cart
        // it can be the same meal, so no duplicate exception check here
        userCart.add(mealId);
        user.setMealCart(userCart);

        // 2. return User for getting all added meals
        return users.update(user);
    }

    @Nonnull
    public User removeMealFromCart(@Nonnull ObjectId userId, @Nonnull ObjectId mealId)
            throws Exception {
        // 1. get the user and its cart
        User user = users.get(userId);
        List<ObjectId> userCart = user.getMealCart();

        // 2. check the mealId, Nonnull did this work?

        // 3. Remove meal
        userCart.remove(mealId);
        return users.update(user);
    }

    public User addNewUser(User user) throws BadRequestException, AlreadyExistsException {
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
