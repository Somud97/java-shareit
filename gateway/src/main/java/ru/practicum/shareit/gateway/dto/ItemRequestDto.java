package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestDto {
	private Long id;

	@NotBlank(message = "Описание запроса не может быть пустым")
	private String description;
}
