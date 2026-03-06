package ru.practicum.shareit.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.CommentDto;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.util.RequestUtils;

@RestController
@RequestMapping("/items")
public class ItemController {

	private static final String USER_HEADER = "X-Sharer-User-Id";

	private final ItemClient itemClient;

	public ItemController(ItemClient itemClient) {
		this.itemClient = itemClient;
	}

	@PostMapping
	public ResponseEntity<byte[]> create(@RequestHeader(USER_HEADER) Long ownerId,
										  @Valid @RequestBody ItemDto itemDto,
										  HttpServletRequest request) {
		return itemClient.create(
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			itemDto
		);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<byte[]> update(@RequestHeader(USER_HEADER) Long ownerId,
										 @PathVariable Long itemId,
										 @RequestBody ItemDto itemDto,
										 HttpServletRequest request) {
		return itemClient.update(
			itemId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			itemDto
		);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<byte[]> findById(@RequestHeader(USER_HEADER) Long userId,
										   @PathVariable Long itemId,
										   HttpServletRequest request) {
		return itemClient.findById(
			itemId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping
	public ResponseEntity<byte[]> findByOwner(@RequestHeader(USER_HEADER) Long ownerId,
											   HttpServletRequest request) {
		return itemClient.findByOwner(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping("/search")
	public ResponseEntity<byte[]> search(@RequestHeader(USER_HEADER) Long userId,
										  @RequestParam("text") String text,
										  HttpServletRequest request) {
		if (text == null || text.isBlank()) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			return ResponseEntity.ok().headers(headers).body("[]".getBytes(java.nio.charset.StandardCharsets.UTF_8));
		}
		return itemClient.search(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<byte[]> addComment(@RequestHeader(USER_HEADER) Long userId,
											 @PathVariable Long itemId,
											 @Valid @RequestBody CommentDto commentDto,
											 HttpServletRequest request) {
		return itemClient.addComment(
			itemId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			commentDto
		);
	}
}
