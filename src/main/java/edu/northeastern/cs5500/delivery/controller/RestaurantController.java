package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Meal;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.Restaurant;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.*;
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

        // Only adds the default restaurants when there is none
        if (restaurants.getAll().isEmpty()) {
            buildDefaultRestaurants();
        }
    }

    private void buildDefaultRestaurants() {
        try {
            addRestaurant(
                    Restaurant.builder()
                            .id(ObjectId.get())
                            .name("HappyLemo")
                            .phone("6606006606")
                            .address("Face to the sea")
                            .menu(buildDefaultMeals())
                            .build());
            addRestaurant(
                    Restaurant.builder()
                            .id(ObjectId.get())
                            .name("KFC")
                            .phone("8808008808")
                            .address("Face to the mountain")
                            .menu(buildDefaultMeals())
                            .build());
            addRestaurant(
                    Restaurant.builder()
                            .id(ObjectId.get())
                            .name("PandaExpress")
                            .phone("1101001101")
                            .address("I am priceless")
                            .menu(buildDefaultMeals())
                            .build());
            log.info("RestaurantController > construct > added default restaurants");
        } catch (Exception e) {
            log.error("RestaurantController > construct > adding default restaurants > failure?");
            e.printStackTrace();
        }
    }

    private List<Meal> buildDefaultMeals() {
        Random random = new Random();
        int countUpperBound = 20;
        int count = random.nextInt(countUpperBound) + 1;
        List<Meal> meals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double price =
                    Math.floor(random.nextInt(countUpperBound * 2) + random.nextDouble() * 100)
                            / 100;
            meals.add(
                    Meal.builder()
                            .mealId(ObjectId.get())
                            .mealName("cupcake")
                            .mealPrice(price)
                            .build());
        }

        return meals;
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
