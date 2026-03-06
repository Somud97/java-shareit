package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(UserService userService,
                           UserRepository userRepository,
                           ItemRepository itemRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemDto create(Long ownerId, Item item, Long requestId) {
        userService.findById(ownerId);

        validateNewItemData(item);

        item.setOwner(userRepository.getReferenceById(ownerId));

        if (requestId != null) {
            itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос вещи не найден: requestId={}", requestId);
                    return new NotFoundException("Запрос не найден.");
                });
            item.setRequest(itemRequestRepository.getReferenceById(requestId));
        }

        Item saved = itemRepository.save(item);
        log.info("Создана вещь: id={}, владелец={}", saved.getId(), ownerId);

        ItemDto dto = new ItemDto();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setDescription(saved.getDescription());
        dto.setAvailable(saved.getAvailable());
        dto.setRequestId(requestId);
        return dto;
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, Item item) {
        Item existing = itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.warn("Попытка обновления несуществующей вещи: id={}", itemId);
                return new NotFoundException("Вещь не найдена.");
            });

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

        Item saved = itemRepository.save(existing);
        log.info("Обновлена вещь: id={}", itemId);

        ItemDto dto = new ItemDto();
        dto.setId(saved.getId());
        dto.setName(saved.getName());
        dto.setDescription(saved.getDescription());
        dto.setAvailable(saved.getAvailable());
        if (saved.getRequest() != null) {
            dto.setRequestId(saved.getRequest().getId());
        }
        return dto;
    }

    @Override
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> {
                log.warn("Вещь не найдена при запросе по id={}", itemId);
                return new NotFoundException("вещь не найдена");
            });
    }

    @Override
    public Collection<Item> findByOwner(Long ownerId) {
        return itemRepository.findByOwner(ownerId);
    }

    @Override
    public Collection<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchByText(text);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым.");
        }

        User author = userService.findById(userId);
        Item item = findById(itemId);

        LocalDateTime now = LocalDateTime.now();
        if (bookingRepository.countCompletedBooking(itemId, userId, now, BookingStatus.APPROVED) <= 0) {
            throw new ValidationException("Комментировать вещь можно только после завершения бронирования.");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreated(now);
        comment.setItem(item);
        comment.setAuthor(author);

        Comment saved = commentRepository.save(comment);
        log.info("Добавлен комментарий к вещи: itemId={}, userId={}", itemId, userId);
        return CommentMapper.toDto(saved);
    }

    @Override
    public ItemDto getItemWithDetails(Long requesterId, Long itemId) {
        Item item = findById(itemId);
        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setComments(commentRepository.findByItemId(itemId).stream()
            .map(CommentMapper::toDto)
            .collect(Collectors.toList()));

        if (item.getOwner() != null && requesterId.equals(item.getOwner().getId())) {
            enrichWithBookingInfo(dto, item.getId());
        }

        return dto;
    }

    @Override
    public Collection<ItemDto> getItemsWithDetailsForOwner(Long ownerId) {
        List<Item> items = findByOwner(ownerId).stream().toList();
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIds(itemIds).stream()
            .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
            .map(item -> {
                ItemDto dto = ItemMapper.toItemDto(item);
                dto.setComments(commentsByItemId.getOrDefault(item.getId(), List.of()).stream()
                    .map(CommentMapper::toDto)
                    .toList());
                enrichWithBookingInfo(dto, item.getId());
                return dto;
            })
            .toList();
    }

    private void enrichWithBookingInfo(ItemDto dto, Long itemId) {
        LocalDateTime now = LocalDateTime.now();

        Booking last = bookingRepository.findLastBooking(itemId, now, BookingStatus.APPROVED);
        Booking next = bookingRepository.findNextBooking(itemId, now, BookingStatus.APPROVED);

        if (last != null) {
            dto.setLastBooking(toShortDto(last));
        }
        if (next != null) {
            dto.setNextBooking(toShortDto(next));
        }
    }

    private BookingShortDto toShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
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

