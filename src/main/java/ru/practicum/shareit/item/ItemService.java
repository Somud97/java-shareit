package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(Long ownerId, Item item);

    Item update(Long ownerId, Long itemId, Item item);

    Item findById(Long itemId);

    Collection<Item> findByOwner(Long ownerId);

    Collection<Item> search(String text);
}

