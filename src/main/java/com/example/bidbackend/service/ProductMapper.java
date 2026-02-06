package com.example.bidbackend.service;

import com.example.bidbackend.config.AppProperties;
import com.example.bidbackend.dto.ProductResponse;
import com.example.bidbackend.model.Bid;
import com.example.bidbackend.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductMapper {
	private ProductMapper() {}

	public static ProductResponse toResponse(Product product, AppProperties appProperties) {
		BigDecimal currentPrice = product.getStartingPrice();
		if (product.getBids() != null && !product.getBids().isEmpty()) {
			Bid top = product.getBids().get(0);
			if (top != null && top.getAmount() != null) {
				currentPrice = top.getAmount();
			}
		}
		String status = LocalDateTime.now().isAfter(product.getEndTime()) ? "SOLD" : "ACTIVE";
		String imageUrl = buildImageUrl(product.getImageFilename(), appProperties);
		return new ProductResponse(
				product.getId(),
				product.getSellerName(),
				product.getName(),
				product.getDescription(),
				product.getStartingPrice(),
				currentPrice,
				product.getEndTime(),
				status,
				imageUrl
		);
	}

	private static String buildImageUrl(String imageFilename, AppProperties appProperties) {
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
