package edu.northeastern.cs5500.delivery.controller;

import static org.mockito.Mockito.doNothing;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.exception.NotExistsException;
import edu.northeastern.cs5500.delivery.model.Courier;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.Collection;
import javax.inject.Provider;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class CourierControllerTest {
    @Mock GenericRepository<Courier> couriers;

    @Mock Provider<OrderController> orderControllerProvider;

    @Mock Courier courier;

    @Mock Collection<Courier> all;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private CourierController courierController;

    @Before
    public void setup() {
        courierController = new CourierController(couriers, orderControllerProvider);
    }

    @Test(expected = BadRequestException.class)
    public void testAddCourierThrowsBadRequestException()
            throws BadRequestException, AlreadyExistsException {
        Mockito.when(courier.isValid()).thenReturn(false);
        courierController.addCourier(courier);
    }

    @Test(expected = AlreadyExistsException.class)
    public void testAddCourierThrowsAlreadyExistsException()
            throws BadRequestException, AlreadyExistsException {
        Mockito.when(courier.isValid()).thenReturn(true);
        Mockito.when(courier.getId()).thenReturn(new ObjectId());
        Mockito.when(couriers.get(Mockito.any())).thenReturn(courier);
        courierController.addCourier(courier);
    }

    @Test
    public void testAddCourierNotThrowExceptions()
            throws BadRequestException, AlreadyExistsException {
        Mockito.when(courier.isValid()).thenReturn(true);
        Mockito.when(courier.getId()).thenReturn(null);
        Mockito.when(couriers.get(Mockito.any())).thenReturn(null);
        Mockito.when(couriers.add(Mockito.any())).thenReturn(courier);
        Assert.assertEquals(courier, courierController.addCourier(courier));
    }

    @Test
    public void testGetCourier() {
        Mockito.when(couriers.get(Mockito.any())).thenReturn(courier);
        Assert.assertEquals(courier, courierController.getCourier(new ObjectId()));
    }

    @Test
    public void testGetCouriers() {
        Mockito.when(couriers.getAll()).thenReturn(all);
        Assert.assertEquals(all, courierController.getCouriers());
    }

    @Test
    public void testDeleteCourier() throws NotExistsException {
        doNothing().when(couriers).delete(Mockito.any());
    }
}
