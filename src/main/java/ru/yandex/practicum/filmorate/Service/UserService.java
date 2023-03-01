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
        if (userStorage.findUserById(user.getId()) == null) {
            log.error("Не найден пользователь c id {}.", user.getId());
            throw new NotFoundException("Пользователь " + user.getId());
        }
        validate(user);
        return userStorage.updateUser(user);
    }

    public User findUserById(Integer id) {
        if (userStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new NotFoundException("Пользователь " + id);
        }
        return userStorage.findUserById(id);
    }


    public User addToFriend(Integer userId, Integer friendId) {
        validateAdd(userId, friendId);
        return userStorage.addToFriend(userId, friendId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        validateAdd(userId, friendId);
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriend(Integer id) {
        if (userStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new ValidationException("Пользователь " + id);
        }
        return userStorage.getUserFriend(id);
    }

    public List<User> getListOfMutualFriends(Integer id, Integer otherId) {
        if (userStorage.findUserById(id).getFriends() == null || userStorage.findUserById(otherId).getFriends() == null) {
            return new ArrayList<>();
        }
        validateAdd(id, otherId);
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

    private void validateAdd(Integer id, Integer friendId) {
        if (friendId < 0 || id < 0) {
            log.error("id не может быть отрицательным");
            throw new NotFoundException("Отрицательный id");
        }
        if (userStorage.findUserById(id) == null) {
            log.error("Не найден пользователь c id {}.", id);
            throw new NotFoundException("Пользователь " + id);
        }
        if (userStorage.findUserById(friendId) == null) {
            log.error("Не найден пользователь c id {}.", friendId);
            throw new NotFoundException("Пользователь " + friendId);
        }
    }
}