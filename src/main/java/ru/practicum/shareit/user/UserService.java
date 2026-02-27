package ru.practicum.shareit.user;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
public interface UserService {
    User create(User user);

    User update(Long userId, User user);

    Collection<User> findAll();

    User findById(Long userId);

    void deleteById(Long userId);
}

