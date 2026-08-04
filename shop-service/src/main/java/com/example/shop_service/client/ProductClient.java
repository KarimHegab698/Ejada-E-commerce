package com.example.shop_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", contextId = "productClient")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductInfoResponse getProduct(@PathVariable("id") Long id);
}
