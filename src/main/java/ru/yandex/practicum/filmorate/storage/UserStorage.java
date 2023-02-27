package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    public Collection<User> getUsersValue();

    public User createUser(User user);

    public User updateUser(User user);

    public User findUserById(Integer id);

    public User addToFriend(Integer userId, Integer friendId);
    public List<User> getUserFriend (Integer id);
    public User deleteFriend(Integer userId, Integer friendId);
    public Boolean checkingThePresenceOfUser (Integer id);
    public List <User> getListOfMutualFriends (Integer id, Integer otherId);
}
