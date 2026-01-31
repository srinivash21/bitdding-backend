package com.example.bidbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final AppProperties appProperties;

	public WebConfig(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadsDir = Path.of(appProperties.uploadsDir()).toAbsolutePath().normalize();
		String location = uploadsDir.toUri().toString();
		registry
				.addResourceHandler("/uploads/**")
				.addResourceLocations(location);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		var mapping = registry.addMapping("/api/**")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(false);

		String[] origins = appProperties.cors() != null ? appProperties.cors().allowedOrigins() : null;
		if (origins == null || origins.length == 0) {
			mapping.allowedOriginPatterns("*");
			return;
		}
		for (String o : origins) {
			if ("*".equals(o)) {
				mapping.allowedOriginPatterns("*");
				return;
			}
		}
		mapping.allowedOrigins(origins);
	}
}
