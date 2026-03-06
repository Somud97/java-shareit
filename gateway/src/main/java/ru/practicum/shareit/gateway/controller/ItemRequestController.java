package ru.practicum.shareit.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.client.ItemRequestClient;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.util.RequestUtils;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

	private final ItemRequestClient itemRequestClient;

	public ItemRequestController(ItemRequestClient itemRequestClient) {
		this.itemRequestClient = itemRequestClient;
	}

	@PostMapping
	public ResponseEntity<byte[]> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
										  HttpServletRequest request) {
		return itemRequestClient.create(
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			itemRequestDto
		);
	}

	@GetMapping
	public ResponseEntity<byte[]> getMyRequests(HttpServletRequest request) {
		return itemRequestClient.getMyRequests(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping("/all")
	public ResponseEntity<byte[]> getAllRequests(HttpServletRequest request) {
		return itemRequestClient.getAllRequests(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<byte[]> getById(@PathVariable Long requestId, HttpServletRequest request) {
		return itemRequestClient.getById(
			requestId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}
}
