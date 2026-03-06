package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	private Long id;
	private String name;

	@Email(message = "Некорректный формат email")
	private String email;
}
