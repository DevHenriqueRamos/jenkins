package com.hramos.simpleapi.services.impl;

import com.hramos.simpleapi.models.UserModel;
import com.hramos.simpleapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Test
    void saveUserTest_HappyPath() {
        UserModel userModel = new UserModel();
        userModel.setEmail("test@email.com");
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        final var response = userService.save(userModel);
        assertNotNull(response);
        assertEquals(UserModel.class, response.getClass());
        assertEquals(userModel.getEmail(), response.getEmail());

        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void findAllTest_HappyPath() {
        UserModel userModel = new UserModel();
        userModel.setEmail("test@email.com");
        when(userRepository.findAll()).thenReturn(List.of(userModel));

        final List<UserModel> response = userService.findAll();
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(UserModel.class, response.getFirst().getClass());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findByIdTest_HappyPath() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new UserModel()));

        final var response = userService.findUserById(UUID.randomUUID());
        assertNotNull(response);
        assertTrue(response.isPresent());
        assertEquals(UserModel.class, response.get().getClass());

        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void findUserTest_HappyPath() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new UserModel()));

        final var response = userService.findUser("user@email.com");
        assertNotNull(response);
        assertTrue(response.isPresent());
        assertEquals(UserModel.class, response.get().getClass());

        verify(userRepository, times(1)).findByEmail(any(String.class));
    }

    @Test
    void deleteTest_HappyPath() {
        doNothing().when(userRepository).delete(any(UserModel.class));

        userService.delete(new UserModel());

        verify(userRepository, times(1)).delete(any(UserModel.class));
    }
}