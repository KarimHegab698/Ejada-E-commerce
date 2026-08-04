package com.example.inventory_service.controller;


import com.example.inventory_service.dto.ProductRequest;
import com.example.inventory_service.dto.ProductResponse;
import com.example.inventory_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(@RequestPart("product") @Valid ProductRequest request, @RequestPart(value = "image", required = false)MultipartFile image){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request, image));
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id){
        return productService.getProduct(id);
    }

    @GetMapping
    public Page<ProductResponse> getAll(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) Boolean isNew,
                                        @RequestParam(required = false) Boolean bestSeller,
                                        @RequestParam(required = false) Boolean onSale,
                                        @PageableDefault(size = 20, sort = "id") Pageable pageable){
        if (category != null)
            return productService.getByCategory(category, pageable);
        if (gender != null)
            return productService.getByGender(gender, pageable);
        if (isNew != null)
            return productService.getByisNew(isNew, pageable);
        if (bestSeller != null)
            return productService.getByBestSeller(bestSeller, pageable);
        if (onSale != null)
            return productService.getByOnSale(onSale, pageable);
        return productService.getAllProducts(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse update(@PathVariable Long id,@RequestPart("product") @Valid ProductRequest request, @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.updateProduct(id, request, image);
    }
}
