package com.database.ormlibrary.order.repo;

import com.database.ormlibrary.order.OrderConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderConfigurationRepo extends CrudRepository<OrderConfigurationEntity, Long> {
}
