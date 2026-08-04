package com.example.inventory_service.repository;

import com.example.inventory_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findByGender(String gender, Pageable pageable);
    Page<Product> findByisNew(Boolean isNew, Pageable pageable);
    Page<Product> findByBestSeller(Boolean bestSeller, Pageable pageable);
    Page<Product> findByOnSale(Boolean onSale, Pageable pageable);
}
