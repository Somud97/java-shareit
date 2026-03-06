package ru.practicum.shareit.gateway.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.config.ShareItServerProperties;

@Component
public class ItemClient extends BaseClient {

	public ItemClient(RestTemplate restTemplate, ShareItServerProperties serverProperties) {
		super(restTemplate, serverProperties);
	}

	public ResponseEntity<byte[]> create(String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.POST, "/items", query, headers, body);
	}

	public ResponseEntity<byte[]> update(Long itemId, String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.PATCH, "/items/" + itemId, query, headers, body);
	}

	public ResponseEntity<byte[]> findById(Long itemId, String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/items/" + itemId, query, headers, null);
	}

	public ResponseEntity<byte[]> findByOwner(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/items", query, headers, null);
	}

	public ResponseEntity<byte[]> search(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/items/search", query, headers, null);
	}

	public ResponseEntity<byte[]> addComment(Long itemId, String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.POST, "/items/" + itemId + "/comment", query, headers, body);
	}
}
