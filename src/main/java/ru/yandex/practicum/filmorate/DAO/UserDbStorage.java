package ru.yandex.practicum.filmorate.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.*;


@Component
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, User> users = new HashMap<>();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<User> getUsersValue() {
        return jdbcTemplate.query("SELECT * FROM Users;", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void createUser(User user) {
        jdbcTemplate.update("INSERT INTO Users VALUES (1,?,?,?,?)", user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday());
    }

    @Override
    public User findUserById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM Users WHERE id = ?;", new Object[]{id}, new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("User not found with id: " + id));
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update("UPDATE Users SET email = ?, login = ?,name = ?, birthday = ? WHERE id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }


    @Override
    public User addToFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);
        if (users.get(userId).getFriends() != null && users.get(userId).getFriends().contains(friendId)) {
            throw new ValidationException("пользователь уже добавил " + friendId + " в друзья");
        }
        user.setFriends(friendId);
        friendUser.setFriends(userId);
        return user;
    }

    @Override
    public List<User> getUserFriend(Integer id) {
        List<User> friends = new ArrayList<>();
        Set<Integer> userSet = users.get(id).getFriends();
        for (Integer user : userSet) {
            friends.add(users.get(user));
        }
        return friends;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);
        user.deleteFriends(friendId);
        friendUser.deleteFriends(userId);
        return user;
    }

    @Override
    public List<User> getListOfMutualFriends(Integer userId, Integer otherId) {
        List<User> friendsNames = new ArrayList<>();
        Set<Integer> userSet = users.get(userId).getFriends();
        Set<Integer> otherUserSet = users.get(otherId).getFriends();
        for (Integer user : userSet) {
            if (otherUserSet.contains(user)) {
                friendsNames.add(users.get(user));
            }
        }
        return friendsNames;
    }
}


