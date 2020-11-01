package edu.northeastern.cs5500.delivery.controller;

import edu.northeastern.cs5500.delivery.exception.AlreadyExistsException;
import edu.northeastern.cs5500.delivery.model.Meal;
import edu.northeastern.cs5500.delivery.model.Restaurant;
import edu.northeastern.cs5500.delivery.repository.GenericRepository;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import java.util.Collection;

@Singleton
@Slf4j
public class MealController {
    private final GenericRepository<Meal> meals;
    private final Provider<RestaurantController> restaurantControllerProvider;

    @Inject
    MealController(GenericRepository<Meal> mealRepository, Provider<RestaurantController> restaurantControllerProvider) {
        meals = mealRepository;
        this.restaurantControllerProvider = restaurantControllerProvider;

        Meal meal = new Meal();
        meal.setId(new ObjectId("abc-def"));
        meal.setMealName("Taco");
        meal.setMealPrice(0.99);
        try {
            addMealToRestaurant(meal);
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
        }
        log.info("MealController > construct");
    }

    @Nonnull
    public Meal getMeal(@Nonnull ObjectId uuid){
        log.debug("MealController > getMeal({}", uuid);
        return meals.get(uuid);
    }

    @Nonnull
    public Collection<Meal> getMeals(){
        log.debug("MealController > getMeals({}");
        return meals.getAll();
    }

    public void deleteMeal(@Nonnull ObjectId mealId) throws AlreadyExistsException{
        log.debug("MealController > deleteMeal(...)");

        // 1. update restaurant
        ObjectId restaurantId = getMeal(mealId).getRestaurantId();
        RestaurantController restaurantController = restaurantControllerProvider.get();
        restaurantController.updateRestaurantRemoveMeal(restaurantId, mealId);


        // 2. delete meal by mealId
        meals.delete(mealId);
    }

    public Meal addMealToRestaurant(Meal meal) throws AlreadyExistsException {
        // Not only create the meal but also link it to a restaurant.
        log.debug("MealController > addMeal(...)");

        // 1. update restaurant
        ObjectId restaurantId = meal.getRestaurantId();
        ObjectId mealId = meal.getId();
        if (mealId != null && meals.get(mealId) != null){
            throw new AlreadyExistsException();
        }
        RestaurantController restaurantController = restaurantControllerProvider.get();
        restaurantController.updateRestaurantAddMeal(restaurantId, mealId);

        // 2. add meal
        return meals.add(meal);
    }
}
