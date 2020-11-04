package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Cart;
import edu.northeastern.cs5500.delivery.model.Meal;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.User;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
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
        userCart.getMeals().compute(meal, (k, v) -> v == null ? 1 : v + 1);

        // 2. return User for getting all added meals
        return users.update(user);
    }

    @Nonnull
    public User removeMealFromCart(@Nonnull User user, @Nonnull Meal meal) throws Exception {
        // 1. get the user and its cart
        Cart cart = user.getMealCart();

        cart.getMeals().compute(meal, (k, v) -> v - 1 == 0 ? null : v - 1);
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
