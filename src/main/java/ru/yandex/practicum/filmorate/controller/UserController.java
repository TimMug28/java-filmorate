package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsersValue();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/user/{userId}")
    public User getUser(@Valid @PathVariable("userId") Long userId){
        return userService.findUserById(userId);
    }

    @PutMapping ("/{id}/friends/{friendId}")
    public User addingToFriends(@Valid @PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.addToFriend(userId, friendId);
    }

    @DeleteMapping  ("/{id}/friends/{friendId}")
    public User deleteFriends(@Valid @PathVariable ("id") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List <User> getUserFriend(@Valid @PathVariable("id") Long userId){
        return userService.getUserFriend(userId);
    }
    @GetMapping("/{id}/friends/common/{otherId}")
    public List <User> getListOfMutualFriends(@Valid @PathVariable("id") Long userId, @PathVariable("otherId") Long otherId){
        return userService.getListOfMutualFriends(userId, otherId);
    }
}

