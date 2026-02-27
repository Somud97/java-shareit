package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO Sprint add-controllers.
 */
@Service
public class ItemServiceImpl implements ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item create(Long ownerId, Item item) {
        User owner = userService.findById(ownerId);

        validateNewItemData(item);

        long id = idSequence.incrementAndGet();
        item.setId(id);
        item.setOwner(owner);

        items.put(id, item);
        log.info("Создана новая вещь: id={}, владелец={}", id, ownerId);
        return item;
    }

    @Override
    public Item update(Long ownerId, Long itemId, Item item) {
        Item existing = items.get(itemId);
        if (existing == null) {
            log.warn("Попытка обновления несуществующей вещи: id={}", itemId);
            throw new NotFoundException("Вещь не найдена.");
        }

        if (existing.getOwner() == null || !ownerId.equals(existing.getOwner().getId())) {
            log.warn("Попытка обновления вещи другим пользователем: вещьId={}, пользовательId={}", itemId, ownerId);
            throw new NotFoundException("Вещь не найдена.");
        }

        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new ValidationException("Название вещи не может быть пустым.");
            }
            existing.setName(item.getName());
        }
        if (item.getDescription() != null) {
            if (item.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не может быть пустым.");
            }
            existing.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existing.setAvailable(item.getAvailable());
        }

        log.info("Обновлена вещь: id={}", itemId);
        return existing;
    }

    @Override
    public Item findById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            log.warn("Вещь не найдена при запросе по id={}", itemId);
            throw new NotFoundException("Вещь не найдена.");
        }
        return item;
    }

    @Override
    public Collection<Item> findByOwner(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() != null && ownerId.equals(item.getOwner().getId())) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public Collection<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String query = text.toLowerCase(Locale.ROOT);
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (Boolean.FALSE.equals(item.getAvailable())) {
                continue;
            }

            String name = item.getName() != null ? item.getName().toLowerCase(Locale.ROOT) : "";
            String description = item.getDescription() != null
                ? item.getDescription().toLowerCase(Locale.ROOT)
                : "";

            if (name.contains(query) || description.contains(query)) {
                result.add(item);
            }
        }

        return result;
    }

    private void validateNewItemData(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Статус доступности вещи должен быть указан.");
        }
    }
}

