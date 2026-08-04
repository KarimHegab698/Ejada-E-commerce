package com.example.wallet_service.repository;

import com.example.wallet_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
    List<Transaction> findByReference(String reference);
}
