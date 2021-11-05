package com.smoothstack.order.repo;

import com.database.ormlibrary.order.OrderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends CrudRepository<OrderEntity, Long> {
    @Query (value = "select * from order_entity where order_entity.user_id = :userId", nativeQuery = true)
    List<OrderEntity> getOrderByUser (@Param("userId") Long userId);

    List<OrderEntity> getOrderEntitiesByDriverId(Long driverId);
}
