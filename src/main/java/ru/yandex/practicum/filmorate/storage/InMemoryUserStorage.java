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
    private final Map<Integer, User> users = new HashMap<>();
    private Integer startID;
    private List<Integer> ids = new ArrayList<>();


    public InMemoryUserStorage() {
        startID = 1;
    }

    @Override
    public Collection<User> getUsersValue() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        Integer id = startID;
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
    public User findUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public User addToFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);
        if(users.get(userId).getFriends() != null) {
            if (users.get(userId).getFriends().contains(friendId)) {
                throw new ValidationException("пользователь уже добавил " + friendId + " в друзья");
            }
        }
        user.setFriends(friendId);
        friendUser.setFriends(userId);
        return user;
    }

    @Override
    public List<User> getUserFriend(Integer id) {
//        User user = users.get(id);
//        List<User> friends = new ArrayList<>();
//        for (Integer friend : user.getFriends()) {
//            friends.add(users.get(friend));
//        }
//        return friends;
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
    public Boolean checkingThePresenceOfUser(Integer id) {
        return !users.containsKey(id);
    }

    @Override
    public List<User> getListOfMutualFriends(Integer userId, Integer otherId) {
//        User user = users.get(userId);
//        User friendUser = users.get(otherId);
//        List<Integer> friends = new ArrayList<>(user.getFriends());
//        friends.retainAll(friendUser.getFriends());
//        List<User> mutualFriends = new ArrayList<>();
//        for (Integer friend : user.getFriends()) {
//            mutualFriends.add(users.get(friend));
//        }
//        return mutualFriends;3

        List<User> friendsNames = new ArrayList<>();
        Set<Integer> userSet = users.get(userId).getFriends();
        Set<Integer> userSet1 = users.get(otherId).getFriends();
        for (Integer user : userSet) {
            if (userSet1.contains(user)) {
                friendsNames.add(users.get(user));
            }
        }
        return friendsNames;
    }


}
