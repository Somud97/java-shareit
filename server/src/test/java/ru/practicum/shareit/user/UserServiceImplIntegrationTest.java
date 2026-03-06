package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void create_persistsUserAndReturnsWithId() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        User created = userService.create(user);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test User");
        assertThat(created.getEmail()).isEqualTo("test@example.com");
        assertThat(userService.findById(created.getId())).isEqualTo(created);
    }

    @Test
    void create_throwsWhenEmailDuplicate() {
        User user = new User();
        user.setName("First");
        user.setEmail("same@example.com");
        userService.create(user);

        User duplicate = new User();
        duplicate.setName("Second");
        duplicate.setEmail("same@example.com");

        assertThatThrownBy(() -> userService.create(duplicate))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void create_throwsWhenEmailInvalid() {
        User user = new User();
        user.setName("Test");
        user.setEmail("no-at-sign");

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(ValidationException.class);
    }
}
