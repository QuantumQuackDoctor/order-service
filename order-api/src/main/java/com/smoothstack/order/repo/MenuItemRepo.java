package com.smoothstack.order.repo;

import com.database.ormlibrary.food.MenuItemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepo extends CrudRepository<MenuItemEntity, Long> {
}
