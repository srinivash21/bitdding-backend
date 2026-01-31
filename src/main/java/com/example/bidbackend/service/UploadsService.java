package com.example.bidbackend.service;

import com.example.bidbackend.config.AppProperties;
import com.example.bidbackend.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadsService {
	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
			"image/jpeg",
			"image/png"
	);

	private final Path uploadsDir;

	public UploadsService(AppProperties appProperties) {
		this.uploadsDir = Path.of(appProperties.uploadsDir()).toAbsolutePath().normalize();
	}

	public String saveImage(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Image file is required");
		}
		String contentType = image.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Only JPG/PNG images are allowed");
		}

		String original = StringUtils.cleanPath(image.getOriginalFilename() == null ? "" : image.getOriginalFilename());
		String extension = guessExtension(original, contentType);
		String filename = UUID.randomUUID() + extension;

		try {
			Files.createDirectories(uploadsDir);
			Path target = uploadsDir.resolve(filename).normalize();
			Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
			return filename;
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image");
		}
	}

	public void deleteIfExists(String filename) {
		if (filename == null || filename.isBlank()) {
			return;
		}
		try {
			Files.deleteIfExists(uploadsDir.resolve(filename).normalize());
		} catch (IOException ignored) {
			// best-effort delete
		}
	}

	private static String guessExtension(String originalFilename, String contentType) {
		String lower = originalFilename.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
			return ".jpg";
		}
		if (lower.endsWith(".png")) {
			return ".png";
		}
		return "image/png".equals(contentType) ? ".png" : ".jpg";
	}
}
