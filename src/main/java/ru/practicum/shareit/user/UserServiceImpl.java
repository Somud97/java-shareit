package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO Sprint add-controllers.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);

    @Override
    public User create(User user) {
        validateNewUser(user);
        ensureEmailUnique(user.getEmail(), null);

        long id = idSequence.incrementAndGet();
        user.setId(id);
        users.put(id, user);
        log.info("Создан новый пользователь: id={}, email={}", id, user.getEmail());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User existing = users.get(userId);
        if (existing == null) {
            log.warn("Попытка обновления несуществующего пользователя: id={}", userId);
            throw new NotFoundException("Пользователь не найден.");
        }

        if (user.getName() != null) {
            existing.setName(user.getName());
        }
        if (user.getEmail() != null) {
            validateEmailFormat(user.getEmail());
            ensureEmailUnique(user.getEmail(), userId);
            existing.setEmail(user.getEmail());
        }

        log.info("Обновлён пользователь: id={}", userId);
        return existing;
    }

    @Override
    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public User findById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            log.warn("Пользователь не найден при запросе по id={}", userId);
            throw new NotFoundException("Пользователь не найден.");
        }
        return user;
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
        log.info("Удалён пользователь: id={}", userId);
    }

    private void validateNewUser(User user) {
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new ValidationException("E-mail пользователя не может быть пустым.");
        }
        validateEmailFormat(email);
    }

    private void validateEmailFormat(String email) {
        if (!email.contains("@")) {
            throw new ValidationException("Указан некорректный адрес e-mail.");
        }
    }

    private void ensureEmailUnique(String email, Long currentUserId) {
        for (User existing : users.values()) {
            if (email.equals(existing.getEmail())
                && (currentUserId == null || !existing.getId().equals(currentUserId))) {
                throw new ConflictException("Пользователь с таким e-mail уже существует.");
            }
        }
    }
}