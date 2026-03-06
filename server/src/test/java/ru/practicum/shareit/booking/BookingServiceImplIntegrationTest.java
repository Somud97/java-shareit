package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    void create_and_getForBooker_workWithDatabase() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner-b@test.ru");
        User savedOwner = userService.create(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.ru");
        User savedBooker = userService.create(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание");
        item.setAvailable(true);
        var createdItemDto = itemService.create(savedOwner.getId(), item, null);

        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(createdItemDto.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(savedBooker.getId(), request);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        Collection<BookingDto> forBooker = bookingService.getForBooker(savedBooker.getId(), BookingState.ALL);
        assertThat(forBooker).hasSize(1);
        assertThat(forBooker.iterator().next().getId()).isEqualTo(created.getId());
    }
}
