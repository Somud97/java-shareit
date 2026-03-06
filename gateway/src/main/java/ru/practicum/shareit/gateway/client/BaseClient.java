package ru.practicum.shareit.gateway.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.config.ShareItServerProperties;

import java.net.URI;

public abstract class BaseClient {

	protected final RestTemplate restTemplate;
	protected final String baseUrl;

	protected BaseClient(RestTemplate restTemplate, ShareItServerProperties serverProperties) {
		this.restTemplate = restTemplate;
		this.baseUrl = serverProperties.getUrl().replaceAll("/$", "");
	}

	protected ResponseEntity<byte[]> exchange(HttpMethod method, String path, String query,
											  HttpHeaders requestHeaders, Object body) {
		String url = baseUrl + path + (query != null && !query.isEmpty() ? "?" + query : "");

		HttpHeaders headers = new HttpHeaders();
		if (requestHeaders != null) {
			requestHeaders.forEach((name, values) -> {
				if (!"content-length".equalsIgnoreCase(name) && values != null) {
					values.forEach(v -> headers.add(name, v));
				}
			});
		}
		if (body != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}

		HttpEntity<Object> entity = new HttpEntity<>(body, headers);

		ResponseEntity<byte[]> response = restTemplate.exchange(
			URI.create(url),
			method,
			entity,
			byte[].class
		);

		HttpHeaders outHeaders = new HttpHeaders();
		if (response.getHeaders().getContentType() != null) {
			outHeaders.setContentType(response.getHeaders().getContentType());
		}
		return new ResponseEntity<>(
			response.getBody(),
			outHeaders,
			response.getStatusCode()
		);
	}
}
