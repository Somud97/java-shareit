package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long ownerId,
                          @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return itemService.create(ownerId, item, itemDto.getRequestId());
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return itemService.update(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_HEADER) Long userId,
                            @PathVariable Long itemId) {
        return itemService.getItemWithDetails(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> findByOwner(@RequestHeader(USER_HEADER) Long ownerId) {
        return itemService.getItemsWithDetailsForOwner(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestHeader(USER_HEADER) Long userId,
                                      @RequestParam("text") String text) {
        return itemService.search(text).stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}