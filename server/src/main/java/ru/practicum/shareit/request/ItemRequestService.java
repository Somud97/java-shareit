package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long requestorId, ItemRequestDto dto);

    List<ItemRequestDto> getMyRequests(Long requestorId);

    List<ItemRequestDto> getAllOtherRequests(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
