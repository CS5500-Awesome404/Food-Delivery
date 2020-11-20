package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.CourierController;
import edu.northeastern.cs5500.delivery.controller.OrderController;
import edu.northeastern.cs5500.delivery.model.Courier;
import edu.northeastern.cs5500.delivery.model.Order;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class CourierView implements View {

    @Inject
    CourierView() {}

    @Inject JsonTransformer jsonTransformer;

    @Inject CourierController courierController;

    @Inject OrderController orderController;

    @Override
    public void register() {
        log.info("CourierView > register");

        get(
                "/courier",
                (request, response) -> {
                    log.info("/courier");
                    response.type("application/json");
                    return courierController.getCouriers();
                },
                jsonTransformer);

        get(
                "/courier/:id",
                (request, response) -> {
                    final String paramId = request.params(":id");
                    log.info("/courier/:id<{}>", paramId);
                    final ObjectId id = new ObjectId(paramId);
                    log.info(id.toString());
                    Courier courier = courierController.getCourier(id);
                    if (courier == null) {
                        halt(404);
                    }
                    response.type("application/json");
                    return courier;
                },
                jsonTransformer);

        post(
                "/courier",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Courier courier = mapper.readValue(request.body(), Courier.class);
                    if (!courier.isValid()) {
                        response.status(400);
                        return "";
                    }

                    // Ignore the user-provided ID if there is one
                    courier.setId(null);
                    courier = courierController.addCourier(courier);

                    response.redirect(
                            String.format("/courier/%s", courier.getId().toHexString()), 301);
                    return courier;
                });

        delete(
                "/courier",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);

                    courierController.deleteCourier(new ObjectId(map.get(ViewUtils.ID)));
                    return map;
                });

        put(
                "/courier",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    HashMap<String, String> map = mapper.readValue(request.body(), HashMap.class);

                    Order order =
                            orderController.getOrder(new ObjectId(map.get(ViewUtils.ORDER_ID)));
                    String operation = map.get(ViewUtils.OPERATION);

                    switch (operation) {
                        case ViewUtils.PICK_UP:
                            courierController.pickUpAnOrder(
                                    new ObjectId(map.get(ViewUtils.COURIER_ID)), order);
                            return order;
                        case ViewUtils.DELIVER:
                            courierController.deliveryAnOrder(
                                    new ObjectId(map.get(ViewUtils.COURIER_ID)), order);
                            return order;
                        default:
                            response.status(400);
                            return "";
                    }
                });
    }
}
