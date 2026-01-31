package com.example.bidbackend.controller;

import com.example.bidbackend.dto.BidRequest;
import com.example.bidbackend.model.Bid;
import com.example.bidbackend.service.BidService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BidController {
	private final BidService bidService;

	public BidController(BidService bidService) {
		this.bidService = bidService;
	}

	@PostMapping("/products/{id}/bids")
	public Bid placeBid(@PathVariable("id") Long productId, @Valid @RequestBody BidRequest request) {
		return bidService.placeBid(productId, request);
	}

	@GetMapping("/products/{id}/bids")
	public List<Bid> listBids(@PathVariable("id") Long productId) {
		return bidService.listBids(productId);
	}
}
