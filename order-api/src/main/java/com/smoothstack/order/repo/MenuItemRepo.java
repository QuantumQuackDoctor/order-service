package com.smoothstack.order.repo;

import com.database.ormlibrary.food.MenuItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends CrudRepository<MenuItemEntity, Long> {

    @Query(value = "SELECT * FROM menu_item_entity mte" +
            "JOIN food_order_entity_order_items foeoi on mte.id = foeoi.order_items_id\n" +
            "where food_order_entity_id = 5", nativeQuery = true)
    List<MenuItemEntity> findItemsByOrderId (Long orderId);
}
