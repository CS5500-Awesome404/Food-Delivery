package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.Restaurant;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.Nonnull;
import javax.inject.Provider;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class RestaurantController {

    private final GenericRepository<Restaurant> restaurants;
    private final Provider<OrderController> orderControllerProvider;

    public RestaurantController(
            GenericRepository<Restaurant> restaurants,
            Provider<OrderController> orderControllerProvider) {
        this.restaurants = restaurants;
        this.orderControllerProvider = orderControllerProvider;
    }

    @Nonnull
    public Restaurant addRestaurant(Restaurant restaurant)
            throws BadRequestException, AlreadyExistsException {
        log.debug("RestaurantController > createNewRestaurant(...)");
        if (!restaurant.isValid()) {
            throw new BadRequestException();
        }

        ObjectId id = restaurant.getId();
        if (id != null && restaurants.get(id) != null) {
            throw new AlreadyExistsException();
        }

        return restaurants.add(restaurant);
    }

    @Nonnull
    public Restaurant getRestaurant(@Nonnull ObjectId uuid) {
        log.debug("RestaurantController > getRestaurant({})", uuid);
        return restaurants.get(uuid);
    }

    @Nonnull
    public Collection<Restaurant> getRestaurants() {
        log.debug("RestaurantController > getRestaurants()");
        return restaurants.getAll();
    }

    public void deleteRestaurant(@Nonnull ObjectId id) throws Exception {
        log.debug("RestaurantController > deleteRestaurant(...)");
        restaurants.delete(id);
    }

    public void updateRestaurantAddMeal(ObjectId restaurantId, ObjectId mealId)
            throws AlreadyExistsException {
        Restaurant restaurant = restaurants.get(restaurantId);
        List<ObjectId> newMenu = restaurant.getMenu();
        // check of duplication.
        if (mealId != null && restaurant.getMenu().contains(mealId)) {
            throw new AlreadyExistsException();
        }
        newMenu.add(mealId);
        restaurant.setMenu(newMenu);
        restaurants.update(restaurant);
    }

    public void updateRestaurantRemoveMeal(ObjectId restaurantId, ObjectId mealId)
            throws AlreadyExistsException {
        Restaurant restaurant = restaurants.get(restaurantId);
        List<ObjectId> newMenu = restaurant.getMenu();
        // check that mealId exists.
        if (mealId != null && restaurant.getMenu().contains(mealId)) {
            throw new AlreadyExistsException();
        }
        newMenu.remove(mealId);
        restaurant.setMenu(newMenu);
        restaurants.update(restaurant);
    }

    public void updateRestaurantName(ObjectId restaurantId, String newRestaurantName) {
        Restaurant restaurant = restaurants.get(restaurantId);
        restaurant.setName(newRestaurantName);
        restaurants.update(restaurant);
    }

    public void makeFoodForOrder(ObjectId orderId) {
        // set up a timer that will use oder controller to update
        // status.

        TimerTask foodReady =
                new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            OrderController orderController = orderControllerProvider.get();
                            try {
                                orderController.updateStatus(orderId, Order.Status.READY);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cancel();
                        }
                    }
                };

        Timer timer = new Timer();
        timer.schedule(foodReady, 10000);
    }
}
