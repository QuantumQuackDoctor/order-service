package com.smoothstack.order.repo;

import com.database.ormlibrary.user.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<UserEntity, Long> {
}
