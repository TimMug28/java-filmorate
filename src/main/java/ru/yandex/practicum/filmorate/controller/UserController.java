package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.UserService;
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
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/users/{id}")
    public User getUser(@Valid @PathVariable("id") Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addingToFriends(@Valid @PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.addToFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriends(@Valid @PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        return userService.deleteFriend(userId, friendId);
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

