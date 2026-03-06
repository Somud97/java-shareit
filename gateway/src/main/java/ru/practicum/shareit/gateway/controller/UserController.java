package ru.practicum.shareit.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.UserDto;
import ru.practicum.shareit.gateway.util.RequestUtils;

@RestController
@RequestMapping(path = "/users")
public class UserController {

	private final UserClient userClient;

	public UserController(UserClient userClient) {
		this.userClient = userClient;
	}

	@PostMapping
	public ResponseEntity<byte[]> create(@Valid @RequestBody UserDto userDto, HttpServletRequest request) {
		return userClient.create(
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			userDto
		);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<byte[]> update(@PathVariable Long userId,
										 @Valid @RequestBody UserDto userDto,
										 HttpServletRequest request) {
		return userClient.update(
			userId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			userDto
		);
	}

	@GetMapping
	public ResponseEntity<byte[]> findAll(HttpServletRequest request) {
		return userClient.findAll(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<byte[]> findById(@PathVariable Long userId, HttpServletRequest request) {
		return userClient.findById(
			userId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<byte[]> delete(@PathVariable Long userId, HttpServletRequest request) {
		return userClient.delete(
			userId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}
}
