package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Long, User> users = new HashMap<>();
    private long startID;
    private List<Long> ids = new ArrayList<>();


    public InMemoryUserStorage() {
        startID = 1;
    }

    @Override
    public Collection<User> getUsersValue() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        Long id = startID;
        ids.add(id);
        startID++;
        user.setId(id);
        users.put(user.getId(), user);
        log.debug("Данные добавлены для пользователя {}.", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Введен несуществующий id", UserService.class);
            throw new ValidationException("Пользователя с id  " + user.getId() + " не существует");
        }
        users.put(user.getId(), user);
        log.debug("Обновлены данные пользователя {}.", user.getId());
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User addToFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);
        user.setFriends(friendId);
        friendUser.setFriends(userId);
        return user;
    }

    @Override
    public List<User> getUserFriend(Long id) {
        User user = users.get(id);
        List<User> friends = new ArrayList<>();
        for (Long friend : user.getFriends()) {
            friends.add(users.get(friend));
        }
        return friends;
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);
        user.deleteFriends(friendId);
        friendUser.deleteFriends(userId);
        return user;
    }

    @Override
    public Boolean checkingThePresenceOfUser(Long id) {
        return !users.containsKey(id);
    }

    @Override
    public List<User> getListOfMutualFriends(Long userId, Long otherId) {
        List<Long> friends = new ArrayList<>();
        User user = users.get(userId);
        User friendUser = users.get(otherId);
        friends = new ArrayList<>(user.getFriends());
        friends.retainAll(friendUser.getFriends());
        List<User> mutualFriends = new ArrayList<>();
        for (Long friend : user.getFriends()) {
            mutualFriends.add(users.get(friend));
        }
        return mutualFriends;
    }



}
