package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
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
public class OrderController {
    private final GenericRepository<Order> orders;
    private final Provider<UserController> userControllerProvider;

    @Inject
    public OrderController(
            GenericRepository<Order> orders, Provider<UserController> userControllerProvider) {
        this.orders = orders;
        this.userControllerProvider = userControllerProvider;
    }

    @Nonnull
    public Order addNewOrder(@Nonnull Order order) throws Exception {
        log.debug("OrderController > createNewOrder(...)");
        if (!order.isValid()) {
            throw new BadRequestException();
        }

        ObjectId id = order.getId();
        if (id != null && orders.get(id) != null) {
            throw new AlreadyExistsException();
        }

        return orders.add(order);
    }

    @Nonnull
    public Order getOrder(@Nonnull ObjectId uuid) {
        log.debug("OrderController > getOrder({}", uuid);
        return orders.get(uuid);
    }

    @Nonnull
    public Collection<Order> getOrders() {
        log.debug("OrderController > getOrders()");
        return orders.getAll();
    }

    public void deleteOrder(@Nonnull ObjectId orderId) throws Exception {
        log.debug("OrderController > deleteOrder(...)");
        orders.delete(orderId);
    }

    @Nonnull
    public Order updateStatus(@Nonnull ObjectId orderId, Order.Status newStatus) throws Exception {
        log.debug("OrderController > updateStatus(...)");
        Order order = orders.get(orderId);
        // No updates, return directly.
        if (order.getStatus().equals(newStatus)) {
            return order;
        }

        switch (order.getStatus()) {
            case CREATED:
                if (!newStatus.equals(Order.Status.PREPARING)) {
                    throw new BadRequestException();
                }
                break;
            case PREPARING:
                if (!newStatus.equals(Order.Status.READY)) {
                    throw new BadRequestException();
                }
                break;
            case READY:
                if (!newStatus.equals(Order.Status.DELIVERING)) {
                    throw new BadRequestException();
                }
                break;
            case DELIVERING:
                if (!newStatus.equals(Order.Status.DELIVERED)) {
                    throw new BadRequestException();
                }
                break;
            case DELIVERED:
                throw new BadRequestException();
            default:
                break;
        }

        order.setStatus(newStatus);
        return orders.update(order);
    }
}
