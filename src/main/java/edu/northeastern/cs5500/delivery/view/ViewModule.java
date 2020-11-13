package edu.northeastern.cs5500.delivery.view;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public class ViewModule {
    @Provides
    @IntoSet
    public View provideDeliveryView(DeliveryView deliveryView) {
        return deliveryView;
    }

    @Provides
    @IntoSet
    public View provideStatusView(StatusView statusView) {
        return statusView;
    }

    @Provides
    @IntoSet
    public View provideRestaurantView(RestaurantView restaurantView) {
        return restaurantView;
    }

    @Provides
    @IntoSet
    public View provideOrderView(OrderView orderView) {
        return orderView;
    }
}
