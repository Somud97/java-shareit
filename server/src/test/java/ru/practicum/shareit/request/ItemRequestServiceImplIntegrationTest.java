package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;

    @Test
    void create_and_getMyRequests_workWithDatabase() {
        User user = new User();
        user.setName("Requester");
        user.setEmail("req@test.ru");
        User savedUser = userService.create(user);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto created = itemRequestService.create(savedUser.getId(), dto);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
        assertThat(created.getCreated()).isNotNull();
        assertThat(created.getItems()).isEmpty();

        List<ItemRequestDto> myRequests = itemRequestService.getMyRequests(savedUser.getId());
        assertThat(myRequests).hasSize(1);
        assertThat(myRequests.get(0).getId()).isEqualTo(created.getId());
        assertThat(myRequests.get(0).getDescription()).isEqualTo("Нужна дрель");
    }
}
