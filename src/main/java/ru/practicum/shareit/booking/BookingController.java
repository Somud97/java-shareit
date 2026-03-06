package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(USER_HEADER) Long userId,
                             @RequestBody BookingRequestDto requestDto) {
        return bookingService.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam("approved") boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_HEADER) Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getForBooker(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        return bookingService.getForBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getForOwner(@RequestHeader(USER_HEADER) Long ownerId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        return bookingService.getForOwner(ownerId, bookingState);
    }
}
