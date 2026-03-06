package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
	private Long id;

	@NotBlank(message = "Текст комментария не может быть пустым")
	private String text;
}
