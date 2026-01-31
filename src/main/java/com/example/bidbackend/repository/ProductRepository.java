package com.example.bidbackend.repository;

import com.example.bidbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findBySellerNameIgnoreCaseOrderByCreatedAtDesc(String sellerName);
	List<Product> findAllByOrderByCreatedAtDesc();
}
