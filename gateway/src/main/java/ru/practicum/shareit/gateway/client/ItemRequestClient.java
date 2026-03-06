package ru.practicum.shareit.gateway.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.config.ShareItServerProperties;

@Component
public class ItemRequestClient extends BaseClient {

	public ItemRequestClient(RestTemplate restTemplate, ShareItServerProperties serverProperties) {
		super(restTemplate, serverProperties);
	}

	public ResponseEntity<byte[]> create(String query, HttpHeaders headers, Object body) {
		return exchange(HttpMethod.POST, "/requests", query, headers, body);
	}

	public ResponseEntity<byte[]> getMyRequests(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/requests", query, headers, null);
	}

	public ResponseEntity<byte[]> getAllRequests(String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/requests/all", query, headers, null);
	}

	public ResponseEntity<byte[]> getById(Long requestId, String query, HttpHeaders headers) {
		return exchange(HttpMethod.GET, "/requests/" + requestId, query, headers, null);
	}
}
