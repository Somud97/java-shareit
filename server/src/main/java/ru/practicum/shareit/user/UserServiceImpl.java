package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User create(User user) {
        validateNewUser(user);
        ensureEmailUnique(user.getEmail(), null);

        User saved = userRepository.save(user);
        log.info("Создан новый пользователь: id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    @Transactional
    public User update(Long userId, User user) {
        User existing = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("Попытка обновления несуществующего пользователя: id={}", userId);
                return new NotFoundException("пользователь не найден");
            });

        if (user.getName() != null) {
            existing.setName(user.getName());
        }
        if (user.getEmail() != null) {
            validateEmailFormat(user.getEmail());
            ensureEmailUnique(user.getEmail(), userId);
            existing.setEmail(user.getEmail());
        }

        log.info("Обновлён пользователь: id={}", userId);
        return userRepository.save(existing);
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("Пользователь не найден при запросе по id={}", userId);
                return new NotFoundException("Пользователь не найден.");
            });
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
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
        boolean exists = currentUserId == null
            ? userRepository.existsByEmail(email)
            : userRepository.existsByEmailAndIdNot(email, currentUserId);

        if (exists) {
            throw new ConflictException("Пользователь с таким e-mail уже существует.");
        }
    }
}