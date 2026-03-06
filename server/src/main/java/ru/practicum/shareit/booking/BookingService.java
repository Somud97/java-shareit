package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Collection;

public interface BookingService {

    BookingDto create(Long userId, BookingRequestDto requestDto);

    BookingDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    Collection<BookingDto> getForBooker(Long userId, BookingState state);

    Collection<BookingDto> getForOwner(Long ownerId, BookingState state);
}
