package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DAO.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
//    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;

//    @Autowired
//    public UserService(UserStorage userStorage) {
//        this.userStorage = userStorage;
//    }

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> getUsersValue() {
        return userDbStorage.getUsersValue();
    }

    public void createUser(User user) {
        validate(user);
        userDbStorage.createUser(user);
    }

    public void updateUser(User user) {
        if (userDbStorage.findUserById(user.getId()) == null) {
            log.error("Не найден пользователь c id {}.", user.getId());
            throw new NotFoundException("Пользователь " + user.getId());
        }
        validate(user);
        userDbStorage.updateUser(user);
    }

    public User findUserById(Integer id) {
        if (userDbStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new NotFoundException("Пользователь " + id);
        }
        return userDbStorage.findUserById(id);
    }


    public void addToFriend(Integer userId, Integer friendId) {
        validateAdd(userId, friendId);
        userDbStorage.addToFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        validateAdd(userId, friendId);
        userDbStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriend(Integer id) {
        if (userDbStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new ValidationException("Пользователь " + id);
        }
        return userDbStorage.getUserFriend(id);
    }

    public List<User> getListOfMutualFriends(Integer id, Integer otherId) {
        if (userDbStorage.findUserById(id).getFriends() == null || userDbStorage.findUserById(otherId).getFriends() == null) {
            return new ArrayList<>();
        }
        validateAdd(id, otherId);
        return userDbStorage.getListOfMutualFriends(id, otherId);
    }

    private void validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Пустой E-mail или отсутствует символ - @", UserService.class);
            throw new ValidationException("Email не должен быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Введен пустой логин, либо он содержит пробелы", UserService.class);
            throw new ValidationException("Логин пустой или содержит пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Указана дата рождения из будущего", UserService.class);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private void validateAdd(Integer id, Integer friendId) {
        if (friendId < 0 || id < 0) {
            log.error("id не может быть отрицательным");
            throw new NotFoundException("Отрицательный id");
        }
        if (userDbStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new NotFoundException("Пользователь " + id);
        }
        if (userDbStorage.findUserById(friendId) == null) {
            log.error("Не найден пользователь c id {}.", friendId);
            throw new NotFoundException("Пользователь " + friendId);
        }
    }
}