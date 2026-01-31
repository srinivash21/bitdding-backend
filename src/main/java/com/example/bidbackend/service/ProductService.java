package com.example.bidbackend.service;

import com.example.bidbackend.dto.ProductUpsertForm;
import com.example.bidbackend.exception.ApiException;
import com.example.bidbackend.model.Product;
import com.example.bidbackend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
	private final ProductRepository productRepository;
	private final UploadsService uploadsService;

	public ProductService(ProductRepository productRepository, UploadsService uploadsService) {
		this.productRepository = productRepository;
		this.uploadsService = uploadsService;
	}

	@Transactional(readOnly = true)
	public List<Product> listAll() {
		return productRepository.findAllByOrderByCreatedAtDesc();
	}

	@Transactional(readOnly = true)
	public List<Product> listBySeller(String sellerName) {
		if (sellerName == null || sellerName.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "sellerName is required");
		}
		return productRepository.findBySellerNameIgnoreCaseOrderByCreatedAtDesc(sellerName);
	}

	@Transactional(readOnly = true)
	public Product getById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
	}

	@Transactional
	public Product create(ProductUpsertForm form) {
		validateUpsert(form, true);

		Product product = new Product();
		product.setSellerName(form.getSellerName().trim());
		product.setName(form.getName().trim());
		product.setDescription(form.getDescription().trim());
		product.setStartingPrice(form.getStartingPrice());
		product.setEndTime(form.getEndTime());

		String filename = uploadsService.saveImage(form.getImage());
		product.setImageFilename(filename);

		return productRepository.save(product);
	}

	@Transactional
	public Product update(Long id, ProductUpsertForm form) {
		Product product = getById(id);
		validateUpsert(form, false);

		String sellerName = form.getSellerName();
		if (sellerName == null || sellerName.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "sellerName is required");
		}
		if (!product.getSellerName().equalsIgnoreCase(sellerName.trim())) {
			throw new ApiException(HttpStatus.FORBIDDEN, "You can only edit your own products");
		}

		if (form.getName() != null && !form.getName().isBlank()) {
			product.setName(form.getName().trim());
		}
		if (form.getDescription() != null && !form.getDescription().isBlank()) {
			product.setDescription(form.getDescription().trim());
		}
		if (form.getStartingPrice() != null) {
			product.setStartingPrice(form.getStartingPrice());
		}
		if (form.getEndTime() != null) {
			product.setEndTime(form.getEndTime());
		}

		if (form.getImage() != null && !form.getImage().isEmpty()) {
			String old = product.getImageFilename();
			String filename = uploadsService.saveImage(form.getImage());
			product.setImageFilename(filename);
			uploadsService.deleteIfExists(old);
		}

		return productRepository.save(product);
	}

	@Transactional
	public void delete(Long id, String sellerName) {
		Product product = getById(id);
		if (sellerName == null || sellerName.isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "sellerName is required");
		}
		if (!product.getSellerName().equalsIgnoreCase(sellerName.trim())) {
			throw new ApiException(HttpStatus.FORBIDDEN, "You can only delete your own products");
		}
		String old = product.getImageFilename();
		productRepository.delete(product);
		uploadsService.deleteIfExists(old);
	}

	private static void validateUpsert(ProductUpsertForm form, boolean isCreate) {
		if (form == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Form is required");
		}
		if (form.getSellerName() == null || form.getSellerName().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "sellerName is required");
		}
		if (isCreate) {
			if (form.getName() == null || form.getName().isBlank()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Product name is required");
			}
			if (form.getDescription() == null || form.getDescription().isBlank()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Description is required");
			}
			if (form.getStartingPrice() == null) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Starting price is required");
			}
			if (form.getEndTime() == null) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "End time is required");
			}
			if (form.getImage() == null || form.getImage().isEmpty()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Image is required");
			}
		}

		if (form.getStartingPrice() != null && form.getStartingPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Starting price must be > 0");
		}
		if (form.getEndTime() != null && form.getEndTime().isBefore(LocalDateTime.now())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "End time must be in the future");
		}
	}
}
