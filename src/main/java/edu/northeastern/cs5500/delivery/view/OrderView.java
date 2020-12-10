package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.OrderController;
import edu.northeastern.cs5500.delivery.controller.UserController;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.User;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class OrderView implements View {

    @Inject
    OrderView() {}

    @Inject JsonTransformer jsonTransformer;
    @Inject OrderController orderController;
    @Inject UserController userController;

    @Override
    public void register() {
        log.info("OrderView > register");

        get(
                "/order",
                (request, response) -> {
                    log.debug("/order");
                    response.type("application/json");
                    return orderController.getOrders();
                },
                jsonTransformer);

        get(
                "/order/:id",
                (request, response) -> {
                    final String paramId = request.params(":id");
                    log.debug("/order/{}", paramId);
                    final ObjectId id = new ObjectId(paramId);
                    Order order = orderController.getOrder(id);
                    if (order == null) {
                        halt(404);
                    }
                    response.type("application/json");
                    return order;
                },
                jsonTransformer);

        post(
                "/order",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);

                    User user = userController.getUser(new ObjectId(map.get(ViewUtils.USER_ID)));

                    if (user == null) {
                        halt(404);
                    }

                    // place order
                    return userController.convertCartToOrder(user);
                },
                jsonTransformer);

        put(
                "/order",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Order order = mapper.readValue(request.body(), Order.class);
                    if (!order.isValid()) {
                        response.status(400);
                        return "";
                    }

                    orderController.updateOrder(order);
                    return order;
                },
                jsonTransformer);

        delete(
                "/order",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);
                    orderController.deleteOrder(new ObjectId(map.get(ViewUtils.ID)));
                    return map;
                },
                jsonTransformer);

        put(
                "/order/status",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);
                    Order order = orderController.getOrder(new ObjectId(map.get(ViewUtils.ID)));
                    if (order == null) {
                        halt(404);
                    }
                    String status = map.get(ViewUtils.STATUS);
                    Order.Status newStatus = Order.Status.valueOf(status.toUpperCase());
                    return orderController.updateStatus(order, newStatus);
                },
                jsonTransformer);
    }
}
