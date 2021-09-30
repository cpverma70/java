package com.demo.persistance;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
