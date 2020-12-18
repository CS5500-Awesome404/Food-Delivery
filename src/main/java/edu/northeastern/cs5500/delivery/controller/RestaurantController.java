package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.exception.BadRequestException;
import edu.northeastern.cs5500.delivery.model.Meal;
import edu.northeastern.cs5500.delivery.model.Order;
import edu.northeastern.cs5500.delivery.model.Restaurant;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;
import java.util.*;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class RestaurantController {

    private final GenericRepository<Restaurant> restaurants;
    private final Provider<OrderController> orderControllerProvider;

    @Inject
    public RestaurantController(
            GenericRepository<Restaurant> restaurants,
            Provider<OrderController> orderControllerProvider) {
        this.restaurants = restaurants;
        this.orderControllerProvider = orderControllerProvider;

        log.info("RestaurantController > construct > adding default restaurants");

        // Only adds the default restaurants when there is none
        if (restaurants.getAll().isEmpty()) {
            buildDefaultRestaurants();
        }
    }

    private void buildDefaultRestaurants() {
        try {
            addRestaurant(
                    Restaurant.builder()
                            .id(ObjectId.get())
                            .name("HappyLemo")
                            .phone("6606006606")
                            .address("Face to the sea")
                            .menu(buildDefaultMeals(0))
                            .build());
            addRestaurant(
                    Restaurant.builder()
                            .id(ObjectId.get())
                            .name("KFC")
                            .phone("8808008808")
                            .address("Face to the mountain")
                            .menu(buildDefaultMeals(1))
                            .build());
            addRestaurant(
                    Restaurant.builder()
                            .id(ObjectId.get())
                            .name("PandaExpress")
                            .phone("1101001101")
                            .address("I am priceless")
                            .menu(buildDefaultMeals(2))
                            .build());
            log.info("RestaurantController > construct > added default restaurants");
        } catch (Exception e) {
            log.error("RestaurantController > construct > adding default restaurants > failure?");
            e.printStackTrace();
        }
    }

    private List<Meal> buildDefaultMeals(int num) {
        Random random = new Random();
        int count = 6;
        List<Meal> meals = new ArrayList<>();
        List<String> imageUrls =
                Arrays.asList(
                        "https://image.shutterstock.com/image-photo/close-braised-beef-short-rib-260nw-530248354.jpg",
                        "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
                        "https://cbsnews1.cbsistatic.com/hub/i/2015/07/01/0b059f60-344d-4ada-baae-e683aff3650a/istock000044051102large.jpg",
                        "https://images.livemint.com/rf/Image-621x414/LiveMint/Period2/2018/07/14/Photos/Processed/iStockphoto-kqbD--621x414@LiveMint.jpg",
                        "https://media-cdn.grubhub.com/image/upload/c_scale,w_1650/q_50,dpr_auto,f_auto,fl_lossy,c_crop,e_vibrance:20,g_center,h_900,w_800/v1539269005/Onboarding/SL/burger-and-fries.jpg",
                        "https://idealsoftware.co.za/wp-content/uploads/2015/08/restaurant-food-4.jpg",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIkmJJsAzuiFcXaw8klfoGGN1mJOQMhdr9mQ&usqp=CAU",
                        "https://www.deputy.com/uploads/2018/10/The-Most-Popular-Menu-Items-That-You-should-Consider-Adding-to-Your-Restaurant_Content-image1-min-1024x569.png",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwfnhRn9_pe22y2TJi01MT58002Lk83oNZ_g&usqp=CAU",
                        "https://restaurantden.com/wp-content/uploads/2017/09/free-stock-food-photography-websites.jpg",
                        "https://www.deputy.com/uploads/2018/10/The-Most-Popular-Menu-Items-That-You-should-Consider-Adding-to-Your-Restaurant_Content-image3-min-1024x569.png",
                        "https://res.cloudinary.com/culturemap-com/image/upload/ar_4:3,c_fill,g_faces:center,w_980/v1591733500/photos/312778_original.jpg",
                        "https://i0.wp.com/www.eatthis.com/wp-content/uploads/2020/11/chilis-chicken-crispers.jpg?resize=640%2C360&ssl=1",
                        "https://img.webmd.com/dtmcms/live/webmd/consumer_assets/site_images/articles/health_tools/worst_restaurant_meals_slideshow/1800ss_getty_rf_ham_cheese_omelet.jpg?resize=650px:*",
                        "https://cdn.takeoutcentral.com/images/home-hero/med-deli-slide.jpg",
                        "https://richmondmagazine.com/downloads/27931/download/Feature_BestRestaurants_RestaurantAdarra_Stuffed-squid_white-beans_greens_ShawneeCustalow_rp1219_teaser.jpg?cb=9072e3a62ddb7a279d5fc9cdbb1089c9",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7M7TdL8dtVuPfAV3ffyJw9oM8jtusNJoNXg&usqp=CAU",
                        "https://media.timeout.com/images/105561656/image.jpg",
                        "https://imagesvc.meredithcorp.io/v3/mm/image?url=https%3A%2F%2Fstatic.onecms.io%2Fwp-content%2Fuploads%2Fsites%2F9%2F2010%2F12%2F201012-ss-dishes-lamb-ragu.jpg",
                        "https://assets3.thrillist.com/v1/image/2856117/414x310/scale;jpeg_quality=65.jpg");

        for (int i = 0; i < count; i++) {
            double price = Math.floor(random.nextInt(10) + random.nextDouble() * 100) / 100;
            meals.add(
                    Meal.builder()
                            .mealId(ObjectId.get())
                            .mealName("Meal " + i)
                            .mealPrice(price)
                            .pictureUrl(imageUrls.get(num * 6 + i))
                            .build());
        }
        return meals;
    }

    @Nonnull
    public Restaurant addRestaurant(Restaurant restaurant)
            throws BadRequestException, AlreadyExistsException {
        log.debug("RestaurantController > createNewRestaurant(...)");
        if (!restaurant.isValid()) {
            throw new BadRequestException();
        }

        ObjectId id = restaurant.getId();
        if (id != null && restaurants.get(id) != null) {
            throw new AlreadyExistsException();
        }

        return restaurants.add(restaurant);
    }

    @Nonnull
    public Restaurant getRestaurant(@Nonnull ObjectId uuid) {
        log.debug("RestaurantController > getRestaurant({})", uuid);
        return restaurants.get(uuid);
    }

    @Nonnull
    public Collection<Restaurant> getRestaurants() {
        log.debug("RestaurantController > getRestaurants()");
        return restaurants.getAll();
    }

    public void deleteRestaurant(@Nonnull ObjectId id) throws Exception {
        log.debug("RestaurantController > deleteRestaurant(...)");
        restaurants.delete(id);
    }

    public void updateRestaurant(Restaurant restaurant) throws Exception {

        log.debug("RestaurantController > updateRestaurant(...)");
        restaurants.update(restaurant);
    }

    public void makeFoodForOrder(Order order) throws Exception {
        // set up a timer that will use oder controller to update
        // status.
        OrderController orderController = orderControllerProvider.get();
        orderController.updateStatus(order, Order.Status.PREPARING);

        TimerTask foodReady =
                new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            OrderController orderController = orderControllerProvider.get();
                            try {
                                orderController.updateStatus(order, Order.Status.READY);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cancel();
                        }
                    }
                };

        Timer timer = new Timer();
        timer.schedule(foodReady, 10000);
    }
}
