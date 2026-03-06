package ru.practicum.shareit.request.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        List<ItemRequestItemDto> itemDtos;
        if (items == null || items.isEmpty()) {
            itemDtos = Collections.emptyList();
        } else {
            itemDtos = items.stream()
                .filter(Objects::nonNull)
                .map(ItemRequestMapper::toItemDto)
                .collect(Collectors.toList());
        }
        dto.setItems(itemDtos);

        return dto;
    }

    private static ItemRequestItemDto toItemDto(Item item) {
        ItemRequestItemDto dto = new ItemRequestItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        if (item.getOwner() != null) {
            dto.setOwnerId(item.getOwner().getId());
        }
        return dto;
    }
}

