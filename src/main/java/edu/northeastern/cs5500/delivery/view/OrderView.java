package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.OrderController;
import edu.northeastern.cs5500.delivery.model.Order;
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
                    Order order = mapper.readValue(request.body(), Order.class);
                    if (!order.isValid()) {
                        response.status(400);
                        return "";
                    }

                    // Ignore the user-provided ID if there is one
                    order.setId(null);
                    order = orderController.addNewOrder(order);
                    response.redirect(
                            String.format("/restaurant/%s", order.getId().toHexString()), 301);
                    return order;
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
                    Order order = mapper.readValue(request.body(), Order.class);

                    orderController.deleteOrder(order.getId());
                    return order;
                },
                jsonTransformer);
    }
}
