package ru.practicum.shareit.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ValidationErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : err.getField())
			.findFirst()
			.orElse("Ошибка валидации");
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", message));
	}
}
