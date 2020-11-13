package edu.northeastern.cs5500.delivery.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.JsonTransformer;
import edu.northeastern.cs5500.delivery.controller.CourierController;
import edu.northeastern.cs5500.delivery.model.Courier;
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

    @Override
    public void register() {
        log.info("CourierView > register");

        get(
                "/courier",
                (request, response) -> {
                    log.info("/courier");
                    response.type("application/json");
                    log.info(
                        courierController
                                    .getCouriers()
                                    .iterator()
                                    .next()
                                    .getId()
                                    .toString());
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
                    if (!delivery.isValid()) {
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
                    Courier courier = mapper.readValue(request.body(), Courier.class);

                    courierController.deleteCourier(courier.getId());
                    return courier;
                });
    }
}