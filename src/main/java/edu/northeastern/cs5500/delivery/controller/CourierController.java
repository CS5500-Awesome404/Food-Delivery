package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.exception.NotExistsException;
import edu.northeastern.cs5500.delivery.model.Courier;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Provider;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Slf4j
public class CourierController {
    static final Logger LOGGER = LoggerFactory.getLogger(CourierController.class);

    private final GenericRepository<Courier> couriers;
    private final Provider<OrderController> orderControllerProvider;

    public CourierController(
            GenericRepository<Courier> couriers,
            Provider<OrderController> orderControllerProvider) {
        this.couriers = couriers;
        this.orderControllerProvider = orderControllerProvider;
    }

    @Nonnull
    public Courier addCourier(Courier courier) throws BadRequestException, AlreadyExistsException {
        LOGGER.debug("CourierController > createNewCourier");
        if (!courier.isValid()) {
            throw new BadRequestException();
        }
        ObjectId id = courier.getId();
        if (id != null && couriers.get(id) != null) {
            throw new AlreadyExistsException();
        }
        return couriers.add(courier);
    }

    @Nonnull
    public Courier getCourier(@Nonnull ObjectId uuid) {
        LOGGER.debug("CourierController > getCourier({})", uuid);
        return couriers.get(uuid);
    }

    @Nonnull
    public Collection<Courier> getCouriers() {
        LOGGER.debug("CourierController > getCouriers()");
        return couriers.getAll();
    }

    public void deleteCourier(@Nonnull ObjectId courierId) throws NotExistsException {
        LOGGER.debug("CourierController > deleteCourier()");
        couriers.delete(courierId);
    }

    public void pickUpAnOrder(ObjectId courierId, ObjectId orderId)
            throws NotExistsException, BadRequestException {
        // check courierId
        if (courierId == null) {
            throw new NotExistsException();
        }

        // check order status
        OrderController orderController = orderControllerProvider.get();
        Order order = orderController.getOrder(orderId);
        if (!order.getStatus().equals("READY")) {
            throw new BadRequestException();
        }
        order.setCourierId(courierId);
        order.setStatus(Order.Status.DELIVERING);
    }

    public void deliveryAnOrder(ObjectId courierId, ObjectId orderId)
            throws NotExistsException, BadRequestException {
        // check courierId
        if (courierId == null) {
            throw new NotExistsException();
        }

        // check order status
        OrderController orderController = orderControllerProvider.get();
        Order order = orderController.getOrder(orderId);
        if (!order.getStatus().equals("DELIVERING")) {
            throw new BadRequestException();
        }
        order.setStatus(Order.Status.DELIVERED);
    }
}
