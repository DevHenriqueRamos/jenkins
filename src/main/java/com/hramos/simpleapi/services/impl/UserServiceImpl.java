package com.hramos.simpleapi.services.impl;

import com.hramos.simpleapi.models.UserModel;
import com.hramos.simpleapi.repositories.UserRepository;
import com.hramos.simpleapi.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserModel save(UserModel userModel) {
        return userRepository.save(userModel);
    }

    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserModel> findUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void delete(UserModel userModel) {
        userRepository.delete(userModel);
    }

    @Override
    public Optional<UserModel> findUserById(UUID id) {
        return userRepository.findById(id);
    }
}
