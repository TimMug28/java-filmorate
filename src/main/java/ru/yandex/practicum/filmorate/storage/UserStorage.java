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

    public User findUserById(Long id);

    public User addToFriend(Long userId, Long friendId);
    public List<User> getUserFriend (Long id);
    public User deleteFriend(Long userId, Long friendId);
    public Boolean checkingThePresenceOfUser (Long id);
    public List <User> getListOfMutualFriends (Long id, Long otherId);
}
