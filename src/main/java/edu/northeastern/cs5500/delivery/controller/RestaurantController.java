package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.Restaurant;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class RestaurantController {

    private final GenericRepository<Restaurant> restaurants;
    private final Provider<OrderController> orderControllerProvider;

    @Inject
    public RestaurantController(
            GenericRepository<Restaurant> restaurants,
            Provider<OrderController> orderControllerProvider) {
        this.restaurants = restaurants;
        this.orderControllerProvider = orderControllerProvider;

        log.info("RestaurantController > construct > adding default restaurants");

        final Restaurant defaultRestaurant1 = new Restaurant();
        defaultRestaurant1.setName("KFC");

        final Restaurant defaultRestaurant2 = new Restaurant();
        defaultRestaurant2.setName("PF Chang's");

        try {
            addRestaurant(defaultRestaurant1);
            addRestaurant(defaultRestaurant2);
        } catch (Exception e) {
            log.error("RestaurantController > construct > adding default restaurants > failure?");
            e.printStackTrace();
        }
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

    public void updateRestaurant(Restaurant restaurant) throws Exception {

        log.debug("RestaurantController > updateRestaurant(...)");
        restaurants.update(restaurant);
    }

    public void makeFoodForOrder(Order order) throws Exception {
        // set up a timer that will use oder controller to update
        // status.
        OrderController orderController = orderControllerProvider.get();
        orderController.updateStatus(order, Order.Status.PREPARING);

        TimerTask foodReady =
                new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            OrderController orderController = orderControllerProvider.get();
                            try {
                                orderController.updateStatus(order, Order.Status.READY);
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
