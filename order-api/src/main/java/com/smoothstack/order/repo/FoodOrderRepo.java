package com.smoothstack.order.repo;

import com.database.ormlibrary.order.FoodOrderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOrderRepo extends CrudRepository<FoodOrderEntity,Long> {
}
