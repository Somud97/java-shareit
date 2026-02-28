package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static Booking toBooking(BookingRequestDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        BookerShortDto booker = new BookerShortDto();
        booker.setId(booking.getBooker().getId());
        dto.setBooker(booker);

        ItemShortDto item = new ItemShortDto();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        dto.setItem(item);
        return dto;
    }
}
