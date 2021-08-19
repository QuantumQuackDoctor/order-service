package com.smoothstack.order.repo;

import com.database.ormlibrary.food.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepo extends CrudRepository<RestaurantEntity, Long> {
}
