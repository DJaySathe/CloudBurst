package com.dssathe.cloudburst.service;

import com.dssathe.cloudburst.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}
