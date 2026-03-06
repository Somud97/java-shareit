package ru.practicum.shareit.gateway.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.Enumeration;

public final class RequestUtils {

	private RequestUtils() {
	}

	public static HttpHeaders copyHeaders(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> names = request.getHeaderNames();
		if (names == null) {
			return headers;
		}
		for (String name : Collections.list(names)) {
			Enumeration<String> values = request.getHeaders(name);
			if (values != null) {
				for (String value : Collections.list(values)) {
					headers.add(name, value);
				}
			}
		}
		return headers;
	}
}
