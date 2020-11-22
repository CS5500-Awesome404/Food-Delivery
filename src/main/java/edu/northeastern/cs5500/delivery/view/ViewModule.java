package edu.northeastern.cs5500.delivery.view;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public class ViewModule {

    @Provides
    @IntoSet
    public View provideUserView(UserView userView) {
        return userView;
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

    @Provides
    @IntoSet
    public View provideCourierView(CourierView courierView) {
        return courierView;
    }
}
