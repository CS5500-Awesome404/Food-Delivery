package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.UserController;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Cart;
import edu.northeastern.cs5500.delivery.model.Meal;
import edu.northeastern.cs5500.delivery.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class UserView implements View {
    @Inject
    UserView() {}

    @Inject JsonTransformer jsonTransformer;

    @Inject UserController userController;

    @Override
    public void register() {
        log.info("UserView > register");

        get(
                "/user",
                (request, response) -> {
                    log.info("/user");
                    response.type("application/json");
                    List<String> userIds =
                            userController.getUsers().stream()
                                    .map(User::getId)
                                    .map(Object::toString)
                                    .collect(Collectors.toList());

                    log.info(userIds.toString());
                    return userController.getUsers();
                },
                jsonTransformer);

        get(
                "/user/:id",
                (request, response) -> {
                    final String paramId = request.params(":id");
                    log.info("/user/:id<{}>", paramId);
                    final ObjectId id = new ObjectId(paramId);
                    log.info(id.toString());
                    User user = userController.getUser(id);
                    if (user == null) {
                        halt(404);
                    }
                    response.type("application/json");
                    return user;
                },
                jsonTransformer);

        post(
                "/user",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);
                    User user =
                            User.builder()
                                    .id(ObjectId.get())
                                    .name(map.get(ViewUtils.NAME))
                                    .email(map.get(ViewUtils.EMAIL))
                                    .address(map.get(ViewUtils.ADDRESS))
                                    .password(map.get(ViewUtils.PASSWORD))
                                    .mealCart(new Cart())
                                    .build();

                    if (!user.isValid()) {
                        response.status(400);
                        return "";
                    }

                    // Ignore the user-provided ID if there is one
                    user.setId(null);
                    user = userController.addNewUser(user);

                    response.redirect(String.format("/user/%s", user.getId().toHexString()), 301);
                    return user;
                });

        put(
                "/user",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);
                    User user = userController.getUser(new ObjectId(map.get(ViewUtils.ID)));
                    if (user == null) {
                        halt(404);
                    }

                    user.setName(map.get(ViewUtils.NAME));
                    user.setEmail(map.get(ViewUtils.EMAIL));
                    user.setAddress(map.get(ViewUtils.ADDRESS));
                    user.setPassword(map.get(ViewUtils.PASSWORD));

                    if (!user.isValid()) {
                        response.status(400);
                        return "";
                    }

                    userController.updateUser(user);
                    return user;
                });

        delete(
                "/user",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);
                    userController.deleteUser(new ObjectId(map.get(ViewUtils.ID)));
                    return map;
                });

        put(
                "/user/cart",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);
                    User user = userController.getUser(new ObjectId(map.get(ViewUtils.ID)));
                    if (user == null) {
                        halt(400);
                    }
                    Meal meal = new Meal();
                    meal.setMealId(new ObjectId(map.get(ViewUtils.MEAL_Id)));
                    meal.setMealName(map.get(ViewUtils.MEAL_NAME));
                    meal.setMealPrice(Double.valueOf(map.get(ViewUtils.MEAL_PRICE)));

                    String operation = map.get(ViewUtils.OPERATION);
                    try {
                        switch (operation) {
                            case ViewUtils.ADD_OPERATION:
                                return userController.addNewMealToCart(user, meal);
                            case ViewUtils.REMOVE_OPERATION:
                                return userController.removeMealFromCart(user, meal);

                            default:
                                response.status(400);
                                return "";
                        }
                    } catch (BadRequestException exception) {
                        response.status(400);
                        return "";
                    }
                });
    }
}
