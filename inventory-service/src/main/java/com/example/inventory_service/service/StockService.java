package com.example.inventory_service.service;

import com.example.inventory_service.dto.StockAdjustRequest;
import com.example.inventory_service.dto.StockResponse;
import com.example.inventory_service.entity.Stock;
import com.example.inventory_service.entity.StockMovement;
import com.example.inventory_service.exception.InsufficientStockException;
import com.example.inventory_service.exception.ProductNotFoundException;
import com.example.inventory_service.repository.StockMovementRepository;
import com.example.inventory_service.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    public StockService(StockRepository stockRepository, StockMovementRepository stockMovementRepository) {
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public StockResponse getStock(Long productId){
        Stock stock = findStockOrThrow(productId);
        return toResponse(stock);
    }

    @Transactional
    public void adjustStock(Long productId, StockAdjustRequest request) {
        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("No stock record for product " + productId));

        switch (request.getStockOperation()) {
            case DEDUCT -> {
                if (stock.getQuantityAvailable() < request.getQuantity()) {
                    throw new InsufficientStockException(
                            "Insufficient stock for product " + productId);
                }

                stock.setQuantityAvailable(
                        stock.getQuantityAvailable() - request.getQuantity());

                recordMovement(productId,
                        StockMovement.Type.DEDUCT,
                        request.getQuantity(),
                        request.getReference());
            }

            case RESTORE -> {
                stock.setQuantityAvailable(
                        stock.getQuantityAvailable() + request.getQuantity());

                recordMovement(productId,
                        StockMovement.Type.RESTORE,
                        request.getQuantity(),
                        request.getReference());
            }
        }

        stockRepository.save(stock);
    }

    private void recordMovement(Long productId, StockMovement.Type type, int quantity, String reference){
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(type)
                .quantity(quantity)
                .reference(reference)
                .build();
        stockMovementRepository.save(movement);
    }

    private Stock findStockOrThrow(Long productId) {
        return stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("No stock record for product " + productId));
    }

    private StockResponse toResponse(Stock stock) {
        return StockResponse.builder()
                .productId(stock.getProductId())
                .quantityAvailable(stock.getQuantityAvailable())
                .build();
    }
}
