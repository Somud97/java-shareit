package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerShortDto booker;
    private ItemShortDto item;
}
