package com.example.bidbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
		String uploadsDir,
		String baseUrl,
		Cors cors
) {
	public record Cors(String[] allowedOrigins) {}
}
