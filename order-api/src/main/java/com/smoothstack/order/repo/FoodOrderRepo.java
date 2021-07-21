package com.smoothstack.order.repo;

import com.database.ormlibrary.order.FoodOrderEntity;
import org.springframework.data.repository.CrudRepository;

public interface FoodOrderRepo extends CrudRepository<FoodOrderEntity, Long> {
}
