package com.example.bidbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
		Long id,
		String sellerName,
		String name,
		String description,
		BigDecimal startingPrice,
		BigDecimal currentPrice,
		LocalDateTime endTime,
		String status,
		String imageFilename
) {}
