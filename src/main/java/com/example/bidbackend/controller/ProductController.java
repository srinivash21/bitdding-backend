package com.example.bidbackend.controller;

import com.example.bidbackend.config.AppProperties;
import com.example.bidbackend.dto.ProductResponse;
import com.example.bidbackend.dto.ProductUpsertForm;
import com.example.bidbackend.model.Product;
import com.example.bidbackend.service.ProductMapper;
import com.example.bidbackend.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
	private final ProductService productService;
	private final AppProperties appProperties;

	public ProductController(ProductService productService, AppProperties appProperties) {
		this.productService = productService;
		this.appProperties = appProperties;
	}

	@GetMapping("/products")
	public List<ProductResponse> listAll() {
		return productService.listAll().stream().map(p -> ProductMapper.toResponse(p, appProperties)).toList();
	}

	@GetMapping("/products/{id}")
	public ProductResponse getById(@PathVariable Long id) {
		Product p = productService.getById(id);
		return ProductMapper.toResponse(p, appProperties);
	}

	@GetMapping("/sellers/{sellerName}/products")
	public List<ProductResponse> listBySeller(@PathVariable String sellerName) {
		return productService.listBySeller(sellerName).stream().map(p -> ProductMapper.toResponse(p, appProperties)).toList();
	}

	@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ProductResponse create(@ModelAttribute ProductUpsertForm form) {
		Product saved = productService.create(form);
		return ProductMapper.toResponse(saved, appProperties);
	}

	@PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ProductResponse update(@PathVariable Long id, @ModelAttribute ProductUpsertForm form) {
		Product saved = productService.update(id, form);
		return ProductMapper.toResponse(saved, appProperties);
	}

	@DeleteMapping("/products/{id}")
	public void delete(@PathVariable Long id, @RequestParam String sellerName) {
		productService.delete(id, sellerName);
	}
}
