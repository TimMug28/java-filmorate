package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return userService.getUsersValue();
    }

    @PostMapping("/users")
    public void createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
    }

    @PutMapping("/users")
    public void updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
    }

    @GetMapping("/users/{id}")
    public User getUser(@Valid @PathVariable("id") Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addingToFriends(@Valid @PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
         userService.addToFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriends(@Valid @PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriend(@Valid @PathVariable("id") Integer userId) {
        return userService.getUserFriend(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getListOfMutualFriends(@Valid @PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherId) {
        return userService.getListOfMutualFriends(userId, otherId);
    }
}

