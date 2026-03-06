package ru.practicum.shareit.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationErrorHandler {

	private static final Logger log = LoggerFactory.getLogger(ValidationErrorHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : err.getField())
			.findFirst()
			.orElse("Ошибка валидации");
		log.warn("Ошибка валидации (400): {}", message);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException e) {
		String message = e.getConstraintViolations().stream()
			.map(v -> v.getMessage() != null ? v.getMessage() : v.getPropertyPath().toString())
			.collect(Collectors.joining("; "));
		if (message.isEmpty()) {
			message = "Некорректный параметр запроса.";
		}
		log.warn("Ошибка валидации параметров (400): {}", message);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("error", message));
	}
}
