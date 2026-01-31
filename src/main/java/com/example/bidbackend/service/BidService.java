package com.example.bidbackend.service;

import com.example.bidbackend.dto.BidRequest;
import com.example.bidbackend.exception.ApiException;
import com.example.bidbackend.model.Bid;
import com.example.bidbackend.model.Product;
import com.example.bidbackend.repository.BidRepository;
import com.example.bidbackend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidService {
	private final ProductRepository productRepository;
	private final BidRepository bidRepository;

	public BidService(ProductRepository productRepository, BidRepository bidRepository) {
		this.productRepository = productRepository;
		this.bidRepository = bidRepository;
	}

	@Transactional
	public Bid placeBid(Long productId, BidRequest request) {
		if (request == null || request.getAmount() == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Bid amount is required");
		}
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));

		if (LocalDateTime.now().isAfter(product.getEndTime())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Sale time is over. Bidding is closed.");
		}

		BigDecimal current = bidRepository.findTopByProductIdOrderByAmountDescCreatedAtDesc(productId)
				.map(Bid::getAmount)
				.orElse(product.getStartingPrice());

		if (request.getAmount().compareTo(current) <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Bid must be greater than current price");
		}

		Bid bid = new Bid();
		bid.setProduct(product);
		bid.setAmount(request.getAmount());
		bid.setBidderName(request.getBidderName());
		return bidRepository.save(bid);
	}

	@Transactional(readOnly = true)
	public List<Bid> listBids(Long productId) {
		return bidRepository.findByProductIdOrderByAmountDescCreatedAtDesc(productId);
	}
}
