package ru.practicum.shareit.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shareit.server")
public class ShareItServerProperties {

	private String url = "http://localhost:9090";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
