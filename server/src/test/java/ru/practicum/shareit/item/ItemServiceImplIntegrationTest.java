package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Test
    void getItemsWithDetailsForOwner_returnsItemsWithDetailsForOwner() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.ru");
        User savedOwner = userService.create(owner);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Аккумуляторная");
        item.setAvailable(true);
        itemService.create(savedOwner.getId(), item, null);

        Collection<ItemDto> result = itemService.getItemsWithDetailsForOwner(savedOwner.getId());

        assertThat(result).hasSize(1);
        ItemDto dto = result.iterator().next();
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Аккумуляторная");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void getItemsWithDetailsForOwner_returnsEmptyWhenOwnerHasNoItems() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner2@test.ru");
        User savedOwner = userService.create(owner);

        Collection<ItemDto> result = itemService.getItemsWithDetailsForOwner(savedOwner.getId());

        assertThat(result).isEmpty();
    }
}
