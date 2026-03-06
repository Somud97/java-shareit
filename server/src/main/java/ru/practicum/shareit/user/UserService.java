package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    User create(User user);

    User update(Long userId, User user);

    Collection<User> findAll();

    User findById(Long userId);

    void deleteById(Long userId);
}

