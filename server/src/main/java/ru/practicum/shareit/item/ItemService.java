package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long ownerId, Item item, Long requestId);

    ItemDto update(Long ownerId, Long itemId, Item item);

    Item findById(Long itemId);

    Collection<Item> findByOwner(Long ownerId);

    Collection<Item> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto getItemWithDetails(Long requesterId, Long itemId);

    Collection<ItemDto> getItemsWithDetailsForOwner(Long ownerId);
}

