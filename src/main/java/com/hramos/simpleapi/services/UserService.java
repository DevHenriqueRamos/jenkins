package com.hramos.simpleapi.services;

import com.hramos.simpleapi.models.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserModel save(UserModel userModel);

    List<UserModel> findAll();

    Optional<UserModel> findUser(String email);

    void delete(UserModel userModel);

    Optional<UserModel> findUserById(UUID id);
}
