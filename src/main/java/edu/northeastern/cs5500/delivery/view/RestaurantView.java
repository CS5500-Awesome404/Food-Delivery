package edu.northeastern.cs5500.delivery.view;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.RestaurantController;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class RestaurantView implements View {
    @Inject
    RestaurantView() {}

    @Inject JsonTransformer jsonTransformer;
    @Inject RestaurantController restaurantController;

    @Override
    public void register() {
        log.info("RestaurantView > register");

        get(
                "/restaurant",
                (request, response) -> {
                    log.debug("/restaurant");
                    response.type("application/json");
                    return restaurantController.getRestaurants();
                },
                jsonTransformer);

        get(
            "/restaurant/:id",
            (request, response) -> {
                final String paramId = request.params(":id");
                log.debug("/restaurant/:id<{}>", paramId);
                final ObjectId id = new ObjectId(paramId);
                Restaurant restaurant = restaurantController.getRestaurant(id);
                if (restaurant == null) {
                    halt(404);
                }
                response.type("application/json");
                return restaurant;
            },
            jsonTransformer);

        post(
            "/restaurant",
            (request, response) -> {
                ObjectMapper mapper = new ObjectMapper();
                Restaurant restaurant = mapper.readValue(request.body(), Restaurant.class);
                if (!restaurant.isValid()) {
                    response.status(400);
                    return "";
                }

                // Ignore the user-provided ID if there is one
                restaurant.setId(null);
                restaurant = restaurantController.addRestaurant(restaurant);

                response.redirect(
                        String.format("/restaurant/{}", delivery.getId().toHexString()), 301);
                return restaurant;
            });

        put(
            "/restaurant",
            (request, response) -> {
                ObjectMapper mapper = new ObjectMapper();
                Restaurant restaurant = mapper.readValue(request.body(), Restaurant.class);
                if (!restaurant.isValid()) {
                    response.status(400);
                    return "";
                }

                restaurantController.updateRestaurantName(restaurant.getId(), restaurant.getName());
                return restaurant;
            });
        
        delete(
            "/delivery",
            (request, response) -> {
                ObjectMapper mapper = new ObjectMapper();
                Restaurant restaurant = mapper.readValue(request.body(), Restaurant.class);

                restaurantController.deleteRestaurant(restaurant.getId());
                return restaurant;
            });
    }
}
