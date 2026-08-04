package com.example.inventory_service.service;

import com.example.inventory_service.dto.ProductRequest;
import com.example.inventory_service.dto.ProductResponse;
import com.example.inventory_service.entity.Product;
import com.example.inventory_service.entity.Review;
import com.example.inventory_service.entity.Stock;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.repository.StockMovementRepository;
import com.example.inventory_service.repository.StockRepository;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.ProviderNotFoundException;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ImageUploadService imageUploadService;

    public ProductService(ProductRepository productRepository, StockRepository stockRepository, StockMovementRepository stockMovementRepository, ImageUploadService imageUploadService) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.imageUploadService = imageUploadService;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile image){
        String imageUrl = (image != null && !image.isEmpty())
                ? imageUploadService.upload(image)
                : request.getImageUrl();

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .category(request.getCategory())
                .gender(request.getGender())
                .isNew(request.getIsNew() == null || request.getIsNew())
                .bestSeller(request.getBestSeller() == null || request.getBestSeller())
                .onSale(request.getOnSale() == null || request.getOnSale())
                .displayPrice(request.getPrice())
                .imageUrl(imageUrl)
                .build();
        product = productRepository.save(product);

        Stock stock = Stock.builder()
                .productId(product.getId())
                .quantityAvailable(request.getInitialQuantity() == null ? 0 : request.getInitialQuantity())
                .build();
        stockRepository.save(stock);
        return toResponse(product, stock);
    }

    public ProductResponse getProduct(Long id){
        Product product = findProductOrThrow(id);
        Stock stock = stockRepository.findByProductId(id).orElse(null);
        return toResponse(product, stock);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable)
                .map(product -> toResponse(product, stockRepository.findByProductId(product.getId()).orElse(null)));
    }

    public Page<ProductResponse> getByCategory(String category, Pageable pageable){
        return productRepository.findByCategory(category, pageable)
                .map(product -> toResponse(product, stockRepository.findByProductId(product.getId()).orElse(null)));
    }

    public Page<ProductResponse> getByGender(String gender, Pageable pageable){
        return productRepository.findByGender(gender, pageable)
                .map(product -> toResponse(product, stockRepository.findByProductId(product.getId()).orElse(null)));
    }

    public Page<ProductResponse> getByisNew(Boolean isNew, Pageable pageable){
        return productRepository.findByisNew(isNew, pageable)
                .map(product -> toResponse(product, stockRepository.findByProductId(product.getId()).orElse(null)));
    }

    public Page<ProductResponse> getByBestSeller(Boolean bestSeller, Pageable pageable){
        return productRepository.findByBestSeller(bestSeller, pageable)
                .map(product -> toResponse(product, stockRepository.findByProductId(product.getId()).orElse(null)));
    }

    public Page<ProductResponse> getByOnSale(Boolean onSale, Pageable pageable){
        return productRepository.findByOnSale(onSale, pageable)
                .map(product -> toResponse(product, stockRepository.findByProductId(product.getId()).orElse(null)));
    }

    @Transactional
    public void deleteProduct(Long id){
        findProductOrThrow(id);

        stockMovementRepository.deleteByProductId(id);
        stockRepository.deleteStockByProductId(id);
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, MultipartFile image) {
        Product product = findProductOrThrow(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCategory(request.getCategory());
        product.setGender(request.getGender());
        product.setIsNew(request.getIsNew() == null ? product.getIsNew() : request.getIsNew());
        product.setBestSeller(request.getBestSeller() == null ? product.getBestSeller() : request.getBestSeller());
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(imageUploadService.upload(image));
        } else if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        product.setOnSale(request.getOnSale());
        product.setDisplayPrice(
                request.getOnSale()
                        ? request.getPrice()
                        : request.getOriginalPrice()
        );
        product = productRepository.save(product);

        Stock stock = stockRepository.findByProductId(id).orElse(null);
        return toResponse(product, stock);
    }

    private Product findProductOrThrow(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Product not found: " + id));
    }

    private ProductResponse toResponse(Product product, Stock stock){
        List<Review> reviews = product.getReviews();
        Double avgRating = reviews.isEmpty() ? null :
                reviews.stream().mapToInt(Review::getRating).average().orElse(0);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .category(product.getCategory())
                .gender(product.getGender())
                .isNew(product.getIsNew())
                .bestSeller(product.getBestSeller())
                .onSale(product.getOnSale())
                .displayPrice(product.getDisplayPrice())
                .imageUrl(product.getImageUrl())
                .averageRating(avgRating)
                .reviewCount(reviews.size())
                .quantityAvailable(stock == null ? 0 : stock.getQuantityAvailable())
                .build();
    }
}
