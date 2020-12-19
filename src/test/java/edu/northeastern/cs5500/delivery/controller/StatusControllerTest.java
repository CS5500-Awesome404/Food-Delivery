package edu.northeastern.cs5500.delivery.controller;

import static org.junit.Assert.*;

import edu.northeastern.cs5500.delivery.model.*;
import org.junit.Assert;
import org.junit.Test;

public class StatusControllerTest {
    StatusController statusController = new StatusController();

    @Test
    public void testGetStatus() {
        Assert.assertEquals(statusController.getStatus(), "OK");
    }
}
