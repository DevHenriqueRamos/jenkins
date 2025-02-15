package com.hramos.simpleapi.controllers;

import com.hramos.simpleapi.models.UserModel;
import com.hramos.simpleapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private UserModel generateUserModel() {
        UserModel userModel = new UserModel();
        Random random = new Random();
        int randomNumber = random.nextInt(1000) * random.nextInt(1000);
        userModel.setName("user" + randomNumber);
        userModel.setEmail("exemplo"+randomNumber+"@email.com");
        userModel.setCreationDate(LocalDateTime.now());
        userModel.setLastUpdateDate(LocalDateTime.now());
        return userRepository.save(userModel);
    }

    @Test
    void welcomeTest_HappyPath() throws Exception {
        mockMvc.perform(get("/users/welcome/{username}", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome John Doe is everything good?"));
    }

    @Test
    void listAllUsersTest_HappyPath() throws Exception {

        UserModel userModel = generateUserModel();
        generateUserModel();

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value(userModel.getEmail()))
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void listUserTest_HappyPath() throws Exception {
        UserModel userModel = generateUserModel();

        mockMvc.perform(get("/users/get/{email}", userModel.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userModel.getEmail()))
                .andExpect(jsonPath("$.name").value(userModel.getName()))
                .andExpect(jsonPath("$.id").value(userModel.getId().toString()));
    }

    @Test
    void listUserTest_UserNotFound() throws Exception {
        mockMvc.perform(get("/users/get/{email}", "notfound@email.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));
    }

    @Test
    void createUserTest_HappyPath() throws Exception {

        String userDTO = """
                {
                    "name": "John Doe",
                    "email": "exemplo@email.com"
                }
                """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("exemplo@email.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void createUserTest_UserAlreadyExists() throws Exception {

        UserModel userModel = generateUserModel();

        String userDTO = """
                {
                    "name": "John Doe",
                    "email": "%s"
                }
                """.formatted(userModel.getEmail());

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value("User already exists"));
    }

    @Test
    void deleteTest_HappyPath() throws Exception {

        UserModel userModel = generateUserModel();

        mockMvc.perform(delete("/users/delete/{id}", userModel.getId().toString()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").value("User deleted"));

        Optional<UserModel> deleteUser = userRepository.findById(userModel.getId());
        assertTrue(deleteUser.isEmpty());
    }

    @Test
    void deleteTest_UserNotFound() throws Exception {

        mockMvc.perform(delete("/users/delete/{id}", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));
    }

    @Test
    void updateUserTest_HappyPath() throws Exception {
        UserModel userModel = generateUserModel();

        String userDTO = """
                {
                    "name": "John Doe",
                    "email": "exemplo@email.com"
                }
                """;

        mockMvc.perform(put("/users/update/{id}", userModel.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON).content(userDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userModel.getId().toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("exemplo@email.com"));
    }

    @Test
    void updateUserTest_UserNotFound() throws Exception {

        mockMvc.perform(put("/users/update/{id}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User not found"));
    }
}