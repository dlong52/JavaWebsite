package com.example.BE.controllers;

import com.example.BE.dto.ProductDTO;
import com.example.BE.models.Product;
import com.example.BE.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductControllers {

    @Autowired
    private ProductServices productService;

    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "collectionId", required = false) Integer collectionId,
            @RequestParam(value = "size", required = false) String size,
//            @RequestParam(value = "status", required = false) bo status,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pagesize", defaultValue = "8") int pagesize) {
        return productService.getAllProductsWithPaginationAndFilter(search, categoryId, collectionId, size, minPrice, maxPrice, page, pagesize);
    }


    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
    }
}
