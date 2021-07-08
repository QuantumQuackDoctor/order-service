package com.database.ormlibrary.order.repo;

import com.database.ormlibrary.order.OrderEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends CrudRepository<OrderEntity, Long> {
}
