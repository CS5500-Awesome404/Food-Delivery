package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.exception.NotExistsException;
import edu.northeastern.cs5500.delivery.model.Courier;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class CourierController {

    private final GenericRepository<Courier> couriers;
    private final Provider<OrderController> orderControllerProvider;

    @Inject
    public CourierController(
            GenericRepository<Courier> couriers,
            Provider<OrderController> orderControllerProvider) {
        this.couriers = couriers;
        this.orderControllerProvider = orderControllerProvider;
    }

    @Nonnull
    public Courier addCourier(Courier courier) throws BadRequestException, AlreadyExistsException {
        log.debug("CourierController > createNewCourier");
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
        log.debug("CourierController > getCourier({})", uuid);
        return couriers.get(uuid);
    }

    @Nonnull
    public Collection<Courier> getCouriers() {
        log.debug("CourierController > getCouriers()");
        return couriers.getAll();
    }

    public void deleteCourier(@Nonnull ObjectId courierId) throws NotExistsException {
        log.debug("CourierController > deleteCourier()");
        couriers.delete(courierId);
    }

    public void pickUpAnOrder(ObjectId courierId, Order order)
            throws NotExistsException, BadRequestException {
        // check courierId
        if (courierId == null) {
            throw new NotExistsException();
        }

        log.info(order.toString());
        if (!order.getStatus().equals(Order.Status.READY)) {
            throw new BadRequestException();
        }
        order.setCourierId(courierId);
        order.setStatus(Order.Status.DELIVERING);
    }

    public void deliveryAnOrder(ObjectId courierId, Order order) throws Exception {
        // check courierId
        if (courierId == null) {
            throw new NotExistsException();
        }

        // check order status
        OrderController orderController = orderControllerProvider.get();
        if (!order.getStatus().equals(Order.Status.DELIVERING)) {
            throw new BadRequestException();
        }
        order.setStatus(Order.Status.DELIVERED);
    }
}
