package com.dssathe.cloudburst.repository;

import com.dssathe.cloudburst.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
