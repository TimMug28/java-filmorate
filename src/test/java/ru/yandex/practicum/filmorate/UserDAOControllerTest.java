package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.DAO.UserDbStorage;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.controller.UserController;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;


import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOControllerTest {
    private UserController controller;
    private User user;
    private User user1;
    private User user2;
    private User userBad;
    private User userBad3;
    private User userBad4;
    private User userBad5;

    @BeforeEach
    public void start() {

        controller = new UserController( new UserService(new UserDbStorage(new JdbcTemplate())));
        createUsers();
    }

    private void createUsers() {
        user = new User("vik@yandex.ru", "gogi", "mogi",
                LocalDate.of(1994, 1, 1));
        user1 = new User("vik1@yandex.ru", "gogi1", "",
                LocalDate.of(1995, 1, 1));
        user2 = new User("vik2@yandex.ru", "gogi2", "mogi2",
                LocalDate.of(1996, 1, 1));
        userBad = new User("vik3@yandex.ru", "bad", "mogi3",
                LocalDate.of(2025, 1, 1));
        userBad3 = new User("vik3yandex.ru", "bad", "mogi3",
                LocalDate.of(2000, 1, 1));
        userBad4 = new User("vik3@yandex.ru", "", "mogi3",
                LocalDate.of(2000, 1, 1));
        userBad5 = new User("vik3@yandex.ru", "vik ka", "mogi3",
                LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldReturnAllUsers() {  // добавление пользователей

        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        Collection<User> users = controller.getUsers();
        assertEquals(3, users.size(), "Хранилище не должно быть пустым.");
        assertTrue(users.contains(user), "Пользователь не добавлен.");
        assertTrue(users.contains(user1), "Пользователь не добавлен.");
        assertTrue(users.contains(user2), "Пользователь не добавлен.");
    }

    @Test
    void addFriend() {
        assertEquals(0, controller.getUsers().size(), "Хранилище должно быть пустым.");
        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        controller.addingToFriends(user.getId(),user1.getId());
        List<User> friends = controller.getUserFriend(user.getId());
        assertEquals(List.of(user1), friends, "Пользователь не найден");
    }

    @Test
    void deleteFriend() {
        assertEquals(0, controller.getUsers().size(), "Хранилище должно быть пустым.");
        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        controller.addingToFriends(user.getId(),user1.getId());
        Collection<User> users = controller.getUsers();
        controller.addingToFriends(user.getId(),user2.getId());
        controller.deleteFriends(user.getId(), user1.getId());
        List<User> friends = controller.getUserFriend(user.getId());
        assertEquals(List.of(user2), friends, "Пользователь не удалён");
    }

    @Test
    void getUserFriends() {
        assertEquals(0, controller.getUsers().size(), "Хранилище должно быть пустым.");
        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        controller.addingToFriends(user.getId(),user1.getId());
        controller.addingToFriends(user.getId(),user2.getId());
        List<User> friends = controller.getUserFriend(user.getId());
        assertEquals(List.of(user1, user2), friends, "Пользователь не найден");
    }

    @Test
    void getListOfMutualFriends() {
        assertEquals(0, controller.getUsers().size(), "Хранилище должно быть пустым.");
        controller.createUser(user);
        controller.createUser(user1);
        controller.createUser(user2);
        controller.addingToFriends(user.getId(),user1.getId());
        controller.addingToFriends(user.getId(),user2.getId());
        List<User> friends = controller.getListOfMutualFriends(user1.getId(), user2.getId());
        assertEquals(List.of(user), friends, "Общие друзья не найдены");
    }

    @Test
    void incorrectEmail() {
        assertThrows(ValidationException.class, () -> controller.createUser(userBad3), "Email содержит символ @");
        assertFalse(controller.getUsers().contains(userBad3), "Пользователь добавлен");
    }

    @Test
    void incorrectLogin() {
        assertThrows(ValidationException.class, () -> controller.createUser(userBad4), "Логин не пустой.");
        assertFalse(controller.getUsers().contains(userBad4), "Пользователь всё же добавлен");
    }

    @Test
    void incorrectLoginWhitespace() {
        assertThrows(ValidationException.class, () -> controller.createUser(userBad5), "Логин не содержит пробелы");
        assertFalse(controller.getUsers().contains(userBad5), "Пользователь всё же добавлен");
    }

    @Test
    void emptyName() {
        controller.createUser(user1);
        assertTrue(controller.getUsers().contains(user1), "Пользователь не добавлен");
        assertEquals(user1.getLogin(), user1.getName(), "Имя и логин разные");
    }

    @Test
    void incorrectBirthday() {
        assertThrows(ValidationException.class, () -> controller.createUser(userBad), "Дата рождения");
        assertFalse(controller.getUsers().contains(userBad), "Пользователь всё же добавлен");
    }
}
