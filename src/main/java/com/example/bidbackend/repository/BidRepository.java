package com.example.bidbackend.repository;

import com.example.bidbackend.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
	Optional<Bid> findTopByProductIdOrderByAmountDescCreatedAtDesc(Long productId);
	List<Bid> findByProductIdOrderByAmountDescCreatedAtDesc(Long productId);
}
