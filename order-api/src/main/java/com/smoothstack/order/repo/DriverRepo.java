package com.smoothstack.order.repo;

import com.database.ormlibrary.driver.DriverEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepo extends CrudRepository<DriverEntity, Long> {
}
