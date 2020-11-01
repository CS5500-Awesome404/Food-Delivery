package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.DeliveryController;
import edu.northeastern.cs5500.delivery.model.Delivery;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class DeliveryView implements View {

    @Inject
    DeliveryView() {}

    @Inject JsonTransformer jsonTransformer;

    @Inject DeliveryController deliveryController;

    @Override
    public void register() {
        log.info("DeliveryView > register");

        get(
                "/delivery",
                (request, response) -> {
                    log.debug("/delivery");
                    response.type("application/json");
                    return deliveryController.getDeliveries();
                },
                jsonTransformer);

        get(
                "/delivery/:id",
                (request, response) -> {
                    final String paramId = request.params(":id");
                    log.debug("/delivery/:id<{}>", paramId);
                    final ObjectId id = new ObjectId(paramId);
                    Delivery delivery = deliveryController.getDelivery(id);
                    if (delivery == null) {
                        halt(404);
                    }
                    response.type("application/json");
                    return delivery;
                },
                jsonTransformer);

        post(
                "/delivery",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Delivery delivery = mapper.readValue(request.body(), Delivery.class);
                    if (!delivery.isValid()) {
                        response.status(400);
                        return "";
                    }

                    // Ignore the user-provided ID if there is one
                    delivery.setId(null);
                    delivery = deliveryController.addDelivery(delivery);

                    response.redirect(
                            String.format("/delivery/{}", delivery.getId().toHexString()), 301);
                    return delivery;
                });

        put(
                "/delivery",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Delivery delivery = mapper.readValue(request.body(), Delivery.class);
                    if (!delivery.isValid()) {
                        response.status(400);
                        return "";
                    }

                    deliveryController.updateDelivery(delivery);
                    return delivery;
                });

        delete(
                "/delivery",
                (request, response) -> {
                    ObjectMapper mapper = new ObjectMapper();
                    Delivery delivery = mapper.readValue(request.body(), Delivery.class);

                    deliveryController.deleteDelivery(delivery.getId());
                    return delivery;
                });
    }
}
