package com.example.bidbackend.controller;

import com.example.bidbackend.config.AppProperties;
import com.example.bidbackend.service.UploadsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class UploadsController {
	private final UploadsService uploadsService;
	private final AppProperties appProperties;

	public UploadsController(UploadsService uploadsService, AppProperties appProperties) {
		this.uploadsService = uploadsService;
		this.appProperties = appProperties;
	}

	/**
	 * Upload a single image file.
	 * Returns: { "filename": "uuid.jpg", "url": "https://example.com/uploads/uuid.jpg" }
	 */
	@PostMapping(value = "image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
		String filename = uploadsService.saveImage(file);
		String imageUrl = buildImageUrl(filename);

		Map<String, String> response = new HashMap<>();
		response.put("filename", filename);
		response.put("url", imageUrl);
		return response;
	}

	private String buildImageUrl(String imageFilename) {
		if (imageFilename == null || imageFilename.isBlank()) {
			return null;
		}
		String baseUrl = appProperties.baseUrl();
		if (baseUrl == null || baseUrl.isBlank()) {
			return "/uploads/" + imageFilename;
		}
		baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
		return baseUrl + "uploads/" + imageFilename;
	}
}
