package com.example.BE.services;

import com.example.BE.dto.ProductDTO;
import com.example.BE.exceptions.ResourceNotFoundException;
import com.example.BE.models.Product;
import com.example.BE.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServices {

    @Autowired
    private ProductRepository productRepository;

//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }

    public Page<Product> getAllProductsWithPaginationAndFilter(String search, Integer categoryId, Integer collectionId, String size, Double minPrice, Double maxPrice, int page, int pagesize) {
        Pageable pageable = PageRequest.of(page, pagesize);

        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Tìm kiếm theo tên và mô tả
            if (search != null && !search.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), "%" + search + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + search + "%")
                ));
            }
            // Lọc theo categoryId
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.join("category").get("categoryId"), categoryId));
            }
            // Lọc theo collectionId
            if (collectionId != null) {
                predicates.add(criteriaBuilder.equal(root.join("collection").get("collectionId"), collectionId));
            }
            // Lọc theo size (giả sử size là một chuỗi)
            if (size != null && !size.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("sizes"), "%" + size + "%"));
            }
            // Lọc theo giá
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0])); // Tạo Predicate cho tất cả điều kiện
        };
        return productRepository.findAll(spec, pageable);
    }



    public ProductDTO getProductById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return convertToProductDTO(product);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    public Product updateProduct(int id, Product productDetails) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStockQuantity(productDetails.getStockQuantity());
            product.setStatus(productDetails.isStatus());
            product.setSalesCount(productDetails.getSalesCount());
            product.setDiscount(productDetails.getDiscount());
            product.setImages(productDetails.getImages());
            product.setSizes(productDetails.getSizes());
            product.setUpdatedAt(new Date()); // Update timestamp
            return productRepository.save(product);
        }
        return null;
    }
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }

    // Phương thức chuyển đổi Product thành ProductDTO
    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setStatus(product.isStatus());
        dto.setSalesCount(product.getSalesCount());
        dto.setDiscount(product.getDiscount());
        dto.setImages(product.getImages());
        dto.setSizes(product.getSizes());
        dto.setCategoryId(product.getCategory().getCategoryId());
        dto.setCollectionId(product.getCollection().getCollectionId());
        return dto;
    }
}
