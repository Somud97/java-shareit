package ru.practicum.shareit.gateway.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.config.ShareItServerProperties;

@Component
public class BookingClient extends BaseClient {

	public BookingClient(RestTemplate restTemplate, ShareItServerProperties serverProperties) {
		super(restTemplate, serverProperties);
	}

	public ResponseEntity<byte[]> create(String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.POST, "/bookings", query, headers, body);
	}

	public ResponseEntity<byte[]> approve(Long bookingId, String query, HttpHeaders headers) {
		return exchange(HttpMethod.PATCH, "/bookings/" + bookingId, query, headers, null);
	}

	public ResponseEntity<byte[]> getById(Long bookingId, String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/bookings/" + bookingId, query, headers, null);
	}

	public ResponseEntity<byte[]> getForBooker(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/bookings", query, headers, null);
	}

	public ResponseEntity<byte[]> getForOwner(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/bookings/owner", query, headers, null);
	}
}
