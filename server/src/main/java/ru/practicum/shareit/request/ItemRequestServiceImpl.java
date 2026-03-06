package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository,
                                  UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ItemRequestDto create(Long requestorId, ItemRequestDto dto) {
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым.");
        }
        User requestor = userService.findById(requestorId);
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription().trim());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        ItemRequest saved = itemRequestRepository.save(request);
        List<Item> items = itemRepository.findByRequest_Id(saved.getId()).stream().toList();
        return ItemRequestMapper.toDto(saved, items);
    }

    @Override
    public List<ItemRequestDto> getMyRequests(Long requestorId) {
        userService.findById(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(requestorId);
        return requests.stream()
            .map(r -> ItemRequestMapper.toDto(r, itemRepository.findByRequest_Id(r.getId()).stream().toList()))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllOtherRequests(Long userId) {
        userService.findById(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId);
        return requests.stream()
            .map(r -> ItemRequestMapper.toDto(r, itemRepository.findByRequest_Id(r.getId()).stream().toList()))
            .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException("Запрос не найден."));
        List<Item> items = itemRepository.findByRequest_Id(requestId).stream().toList();
        return ItemRequestMapper.toDto(request, items);
    }
}
