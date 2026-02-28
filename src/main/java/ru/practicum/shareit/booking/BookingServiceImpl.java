package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserService userService,
                              ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto requestDto) {
        if (requestDto.getStart() == null || requestDto.getEnd() == null
            || !requestDto.getEnd().isAfter(requestDto.getStart())) {
            throw new ValidationException("Некорректный интервал бронирования");
        }

        User booker = userService.findById(userId);
        Item item = itemService.findById(requestDto.getItemId());

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь.");
        }

        Booking booking = BookingMapper.toBooking(requestDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking saved = bookingRepository.save(booking);
        log.info("Создан запрос на бронирование: id={}, bookerId={}", saved.getId(), userId);
        return BookingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Подтверждение может выполнить только владелец вещи.");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменён.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        log.info("Изменён статус бронирования: id={}, статус={}", saved.getId(), saved.getStatus());
        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!userId.equals(ownerId) && !userId.equals(bookerId)) {
            throw new NotFoundException("Доступ к бронированию запрещён.");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public Collection<BookingDto> getForBooker(Long userId, BookingState state) {
        userService.findById(userId);
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookings;

        switch (state) {
            case CURRENT -> bookings = bookingRepository.findCurrentByBooker(userId, now);
            case PAST -> bookings = bookingRepository.findPastByBooker(userId, now);
            case FUTURE -> bookings = bookingRepository.findFutureByBooker(userId, now);
            case WAITING -> bookings = bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED);
            case ALL -> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            default -> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }

        return bookings.stream()
            .map(BookingMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getForOwner(Long ownerId, BookingState state) {
        userService.findById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookings;

        switch (state) {
            case CURRENT -> bookings = bookingRepository.findCurrentByOwner(ownerId, now);
            case PAST -> bookings = bookingRepository.findPastByOwner(ownerId, now);
            case FUTURE -> bookings = bookingRepository.findFutureByOwner(ownerId, now);
            case WAITING -> bookings = bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
            case ALL -> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
            default -> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
        }

        return bookings.stream()
            .map(BookingMapper::toDto)
            .collect(Collectors.toList());
    }
}
