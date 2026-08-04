package com.example.wallet_service.service;

import com.example.wallet_service.dto.PaymentRequest;
import com.example.wallet_service.dto.PaymentResponse;
import com.example.wallet_service.dto.TransactionResponse;
import com.example.wallet_service.dto.WalletResponse;
import com.example.wallet_service.entity.Transaction;
import com.example.wallet_service.entity.Wallet;
import com.example.wallet_service.exception.InsufficientBalanceException;
import com.example.wallet_service.exception.WalletNotFoundException;
import com.example.wallet_service.repository.TransactionRepository;
import com.example.wallet_service.repository.UserRepository;
import com.example.wallet_service.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    public final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public WalletService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponse createWallet(Long userId){
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .currency("EGP")
                .build();
        return toResponse(walletRepository.save(wallet));
    }

    public WalletResponse getWalletByUserId(Long userId){
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("No wallet found for user " + userId));
        return toResponse(wallet);
    }

    @Transactional
    public WalletResponse deposit(Long walletId, BigDecimal amount){
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        recordTransaction(wallet, Transaction.Type.DEPOSIT, amount, null, Transaction.Status.COMPLETED);
        return toResponse(wallet);
    }

    @Transactional
    public WalletResponse withdraw(Long walletId, BigDecimal amount){
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
        if(wallet.getBalance().compareTo(amount) < 0){
            throw new InsufficientBalanceException("Insufficient balance in wallet " + walletId);
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        recordTransaction(wallet, Transaction.Type.WITHDRAWAL, amount, null, Transaction.Status.COMPLETED);
        return toResponse(wallet);
    }

    @Transactional
    public PaymentResponse pay(Long walletId, PaymentRequest request){
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
        if(wallet.getBalance().compareTo(request.getAmount()) < 0){
            Transaction failed = recordTransaction(wallet, Transaction.Type.PAYMENT, request.getAmount(), request.getReference(), Transaction.Status.FAILED);
            throw new InsufficientBalanceException("Insufficient balance to pay for order " + request.getReference());
        }
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        Transaction txn = recordTransaction(wallet, Transaction.Type.PAYMENT, request.getAmount(), request.getReference(), Transaction.Status.COMPLETED);
        return PaymentResponse.builder()
                .transactionId(txn.getId())
                .status(txn.getStatus().name())
                .balanceAfter(txn.getBalanceAfter())
                .build();
    }

    public List<TransactionResponse> getTransactions(Long walletId){
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Transaction recordTransaction(Wallet wallet, Transaction.Type type, BigDecimal amount,
                                          String reference, Transaction.Status status) {
        Transaction txn = Transaction.builder()
                .walletId(wallet.getId())
                .type(type)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .reference(reference)
                .status(status)
                .build();
        return transactionRepository.save(txn);
    }

    private Wallet findWalletOrThrow(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }

    private TransactionResponse toResponse(Transaction txn) {
        return TransactionResponse.builder()
                .id(txn.getId())
                .type(txn.getType().name())
                .amount(txn.getAmount())
                .balanceAfter(txn.getBalanceAfter())
                .reference(txn.getReference())
                .status(txn.getStatus().name())
                .createdAt(txn.getCreatedAt())
                .build();
    }
}
