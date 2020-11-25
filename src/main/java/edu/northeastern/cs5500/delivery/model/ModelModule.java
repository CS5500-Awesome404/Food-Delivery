package edu.northeastern.cs5500.delivery.model;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelModule {

    @Provides
    public Class<User> provideUserClass() {
        return User.class;
    }

    @Provides
    public Class<Order> provideOrderClass() {
        return Order.class;
    }

    @Provides
    public Class<Restaurant> provideRestaurantClass() {
        return Restaurant.class;
    }
}
