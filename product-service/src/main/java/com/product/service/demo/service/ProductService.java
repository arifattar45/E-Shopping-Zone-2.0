package com.product.service.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.product.service.demo.dto.ProductRequest;
import com.product.service.demo.entity.Product;
import com.product.service.demo.exception.PageNotFoundException;
import com.product.service.demo.exception.ProductNotFoundException;
import com.product.service.demo.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository repo;

	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}

	public Product add(ProductRequest req) {
		Product p = new Product();
		p.setName(req.getName());
		p.setDescription(req.getDescription());
		p.setPrice(req.getPrice());
		p.setCategory(req.getCategory());
		p.setBrand(req.getBrand());
		return repo.save(p);
	}

	public Page<Product> getAll(String name, String category,
	                            Double min, Double max,
	                            String sort, int page, int size) {

	    Sort sorting = Sort.unsorted();

	    if (sort != null && sort.contains(",")) {
	        String[] parts = sort.split(",");
	        sorting = parts[1].equalsIgnoreCase("desc")
	                ? Sort.by(parts[0]).descending()
	                : Sort.by(parts[0]).ascending();
	    }

	    Pageable pageable = PageRequest.of(page, size, sorting);

	    Page<Product> result = repo.searchProducts(name, category, min, max, pageable);
	    
	    if (page >= result.getTotalPages() && result.getTotalPages() > 0) {
	        throw new PageNotFoundException("Page " + page + " does not exist");
	    }

	    if (result.getContent().isEmpty()) {
	        throw new RuntimeException("No products found");
	    }

	    return result;
	}

	public Product getById(Long id) {
		return repo.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
	}

	public Product update(Long id, ProductRequest req) {

		Product product = repo.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

		product.setName(req.getName());
		product.setDescription(req.getDescription());
		product.setPrice(req.getPrice());
		product.setCategory(req.getCategory());
		product.setBrand(req.getBrand());

		return repo.save(product);
	}

	public void delete(Long id) {

		Product product = repo.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

		repo.delete(product);
	}

}
