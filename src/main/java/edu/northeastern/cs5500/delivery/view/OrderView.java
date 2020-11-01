package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.patch;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.controller.OrderController;
import edu.northeastern.cs5500.delivery.model.Order;
import javax.inject.Inject;

public class OrderView implements View {

    @Inject OrderController orderController;

    @Override
    public void register() {
        patch(
                "/order",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Order order = mapper.readValue(request.body(), Order.class);
                    if (!order.isValid()) {
                        response.status(400);
                        return "";
                    }

                    // This is a pick up.
                    if (order.getStatus().equals(Order.Status.DELIVERING)) {}

                    // orderController.updateOrder(order);
                    return order;
                });
    }
}
