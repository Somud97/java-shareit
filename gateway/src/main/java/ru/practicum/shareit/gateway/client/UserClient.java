package ru.practicum.shareit.gateway.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.config.ShareItServerProperties;

@Component
public class UserClient extends BaseClient {

	public UserClient(RestTemplate restTemplate, ShareItServerProperties serverProperties) {
		super(restTemplate, serverProperties);
	}

	public ResponseEntity<byte[]> create(String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.POST, "/users", query, headers, body);
	}

	public ResponseEntity<byte[]> update(Long userId, String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.PATCH, "/users/" + userId, query, headers, body);
	}

	public ResponseEntity<byte[]> findAll(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/users", query, headers, null);
	}

	public ResponseEntity<byte[]> findById(Long userId, String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/users/" + userId, query, headers, null);
	}

	public ResponseEntity<byte[]> delete(Long userId, String query, HttpHeaders headers) {
		return exchange(HttpMethod.DELETE, "/users/" + userId, query, headers, null);
	}
}
