package ru.practicum.shareit.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.BookingRequestDto;
import ru.practicum.shareit.gateway.util.RequestUtils;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

	private static final String USER_HEADER = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	public BookingController(BookingClient bookingClient) {
		this.bookingClient = bookingClient;
	}

	@PostMapping
	public ResponseEntity<byte[]> create(@RequestHeader(USER_HEADER) Long userId,
										  @Valid @RequestBody BookingRequestDto requestDto,
										  HttpServletRequest request) {
		return bookingClient.create(
			request.getQueryString(),
			RequestUtils.copyHeaders(request),
			requestDto
		);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<byte[]> approve(@RequestHeader(USER_HEADER) Long ownerId,
										  @PathVariable Long bookingId,
										  @RequestParam("approved") boolean approved,
										  HttpServletRequest request) {
		return bookingClient.approve(
			bookingId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<byte[]> getById(@RequestHeader(USER_HEADER) Long userId,
										  @PathVariable Long bookingId,
										  HttpServletRequest request) {
		return bookingClient.getById(
			bookingId,
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping
	public ResponseEntity<byte[]> getForBooker(@RequestHeader(USER_HEADER) Long userId,
											   @RequestParam(name = "state", defaultValue = "ALL") String state,
											   HttpServletRequest request) {
		return bookingClient.getForBooker(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}

	@GetMapping("/owner")
	public ResponseEntity<byte[]> getForOwner(@RequestHeader(USER_HEADER) Long ownerId,
											 @RequestParam(name = "state", defaultValue = "ALL") String state,
											 HttpServletRequest request) {
		return bookingClient.getForOwner(
			request.getQueryString(),
			RequestUtils.copyHeaders(request)
		);
	}
}
