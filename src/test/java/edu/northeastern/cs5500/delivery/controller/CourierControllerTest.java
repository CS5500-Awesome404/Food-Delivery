package edu.northeastern.cs5500.delivery.controller;

import static edu.northeastern.cs5500.delivery.model.Order.Status.DELIVERED;
import static edu.northeastern.cs5500.delivery.model.Order.Status.DELIVERING;
import static edu.northeastern.cs5500.delivery.model.Order.Status.READY;
import static org.junit.Assert.*;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.exception.NotExistsException;
import edu.northeastern.cs5500.delivery.model.*;
import edu.northeastern.cs5500.delivery.repository.InMemoryRepository;
import java.util.Collection;
import javax.inject.Provider;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CourierControllerTest {
  CourierController courierController;
  OrderController orderController;
  UserController userController;
  Courier courier1;
  Order order1;

  @Before
  public void setUp() {
    courierController = new CourierController(new InMemoryRepository<>(),
        new Provider<OrderController>() {
      @Override
      public OrderController get() {
        return orderController;
      }
    });
    orderController = new OrderController(new InMemoryRepository<>(), () -> userController);
    userController = new UserController(new InMemoryRepository<>(), () -> orderController);
    courier1 = Courier.builder()
                      .id(ObjectId.get())
                      .name("John")
                      .location("1st ave n")
                      .build();
    order1 = Order.builder()
                  .id(ObjectId.get())
                  .status(READY)
                  .build();
  }

  @Test
  public void testAddCourier() throws BadRequestException, AlreadyExistsException {
    courierController.addCourier(courier1);
    Collection<Courier> couriers = courierController.getCouriers();
    Assert.assertFalse(couriers.isEmpty());
  }

  @Test
  public void testGetCourier() throws BadRequestException, AlreadyExistsException {
    courierController.addCourier(courier1);
    Courier c = courierController.getCourier(courier1.getId());
    Assert.assertEquals(c, courier1);
  }

  @Test
  public void testGetCouriers() throws BadRequestException, AlreadyExistsException {
    courierController.addCourier(courier1);
    Collection<Courier> couriers = courierController.getCouriers();
    Assert.assertEquals(couriers.size(), 1);
  }

  @Test
  public void testDeleteCourier()
      throws BadRequestException, AlreadyExistsException, NotExistsException {
    courierController.addCourier(courier1);
    Collection<Courier> couriers = courierController.getCouriers();
    Assert.assertEquals(couriers.size(), 1);
    courierController.deleteCourier(courier1.getId());
    Assert.assertEquals(couriers.size(), 0);
  }

  @Test
  public void testPickUpAnOrder() throws BadRequestException, NotExistsException {
    courierController.pickUpAnOrder(courier1.getId(), order1);
    Assert.assertEquals(order1.getStatus(), DELIVERING);
  }

  @Test
  public void testDeliveryAnOrder() throws Exception {
    courierController.pickUpAnOrder(courier1.getId(), order1);
    Assert.assertEquals(order1.getStatus(), DELIVERING);
    courierController.deliveryAnOrder(courier1.getId(), order1);
    Assert.assertEquals(order1.getStatus(), DELIVERED);
  }
}
