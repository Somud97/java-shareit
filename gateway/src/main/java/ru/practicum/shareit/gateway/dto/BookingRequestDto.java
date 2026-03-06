package ru.practicum.shareit.gateway.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {

	@NotNull(message = "ID вещи обязателен")
	private Long itemId;

	@NotNull(message = "Дата начала бронирования обязательна")
	private LocalDateTime start;

	@NotNull(message = "Дата окончания бронирования обязательна")
	private LocalDateTime end;
}
