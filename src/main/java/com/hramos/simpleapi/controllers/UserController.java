package com.hramos.simpleapi.controllers;

import com.hramos.simpleapi.dtos.UserDTO;
import com.hramos.simpleapi.models.UserModel;
import com.hramos.simpleapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/welcome/{username}")
    public ResponseEntity<Map<String, Object>> welcome(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome " + username + " is everything good?");
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<List<UserModel>> listAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<Object> listUser(@PathVariable String email) {
        Optional<UserModel> userModelOptional = userService.findUser(email);

        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody UserDTO userDTO) {

        Optional<UserModel> userModelOptional = userService.findUser(userDTO.getEmail());

        if (userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        UserModel userModel = new UserModel();
        userModel.setName(userDTO.getName());
        userModel.setEmail(userDTO.getEmail());
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
        Optional<UserModel> userModelOptional = userService.findUserById(id);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        userService.delete(userModelOptional.get());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        Optional<UserModel> userModelOptional = userService.findUserById(id);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        UserModel userModel = userModelOptional.get();
        userModel.setName(userDTO.getName());
        userModel.setEmail(userDTO.getEmail());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
    }
}
