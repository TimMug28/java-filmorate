package ru.yandex.practicum.filmorate.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsersValue() {
        return userStorage.getUsersValue();
    }

    public User createUser(User user) {
        validate(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }


    public User addToFriend(Long userId, Long friendId) {
        validateAdd(userId, friendId);
        return userStorage.addToFriend(userId, friendId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (userStorage.checkingThePresenceOfUser(userId)) {
            throw new NotFoundException("404");
        }
        if (userStorage.checkingThePresenceOfUser(friendId)) {
            throw new NotFoundException("404");
        }
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriend(Long id) {
        return userStorage.getUserFriend(id);
    }

    public List<User> getListOfMutualFriends(Long id, Long otherId) {
        return userStorage.getListOfMutualFriends(id, otherId);
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

    private void validateAdd(Long id, Long friendId) {
        if (userStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new NotFoundException("Пользователь " + id);
        }
        if (userStorage.findUserById(friendId) == null) {
            log.error("Не найден пользователь c id {}.", friendId);
            throw new NotFoundException("Пользователь " + friendId);
        }
        if (userStorage.findUserById(id).getFriends().contains(friendId)) {
            throw new ValidationException("Пользователь c id " + friendId + " уже добавлен в друзья");
        }
    }

}