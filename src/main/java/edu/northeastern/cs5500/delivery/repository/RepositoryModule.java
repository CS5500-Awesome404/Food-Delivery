// To use the in-memory module:
//
// package edu.northeastern.cs5500.delivery.repository;

// import dagger.Module;
// import dagger.Provides;
// import edu.northeastern.cs5500.delivery.model.Delivery;
// import edu.northeastern.cs5500.delivery.model.Order;

// @Module
// public class RepositoryModule {
//     @Provides
//     public GenericRepository<Delivery> provideDeliveryRepository() {
//         return new InMemoryRepository<>();
//     }

//     @Provides
//     public GenericRepository<Order> provideOrderRepository() {
//         return new InMemoryRepository<>();
//     }
// }
package edu.northeastern.cs5500.delivery.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.delivery.model.Courier;
import edu.northeastern.cs5500.delivery.model.Delivery;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.Restaurant;
import edu.northeastern.cs5500.delivery.model.User;

@Module
public class RepositoryModule {
    @Provides
    public GenericRepository<Delivery> provideDeliveryRepository() {
        return new InMemoryRepository<>();
    }

    @Provides
    public GenericRepository<Restaurant> provideRestaurantRepository() {
        return new InMemoryRepository<>();
    }

    @Provides
    public GenericRepository<User> provideUserRepository() {
        return new InMemoryRepository<>();
    }

    @Provides
    public GenericRepository<Courier> provideCourierRepository() {
        return new InMemoryRepository<>();
    }

    @Provides
    public GenericRepository<Order> provideOrderRepository() {
        return new InMemoryRepository<>();
    }
}

/*
// Here's an example of how you imght swap out the in-memory repository for a database-backed
// repository:


    @Provides
    public GenericRepository<User> provideUserRepository(MongoDBService mongoDBService) {
        return new MongoDBRepository<>(User.class, mongoDBService);
    }

    @Provides
    public GenericRepository<Courier> provideCourierRepository(MongoDBService mongoDBService) {
        return new MongoDBRepository<>(Courier.class, mongoDBService);
    }

    @Provides
    public GenericRepository<Restaurant> provideRestaurantRepository(
            MongoDBService mongoDBService) {
        return new MongoDBRepository<>(Restaurant.class, mongoDBService);
    }

    @Provides
    public GenericRepository<Order> provideOrderRepository(MongoDBService mongoDBService) {
        return new MongoDBRepository<>(Order.class, mongoDBService);
    }
    */
