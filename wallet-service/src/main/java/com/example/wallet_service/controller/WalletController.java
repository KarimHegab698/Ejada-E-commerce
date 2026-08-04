package com.example.wallet_service.controller;

import com.example.wallet_service.dto.*;
import com.example.wallet_service.security.CurrentUser;
import com.example.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public WalletResponse getMyWallet(){
        return walletService.getWalletByUserId(CurrentUser.id());
    }

    @PostMapping("/me/deposit")
    public WalletResponse deposit(@Valid @RequestBody AmountRequest request){
        WalletResponse wallet = walletService.getWalletByUserId(CurrentUser.id());
        return walletService.deposit(wallet.getId(), request.getAmount());
    }

    @PostMapping("/me/withdraw")
    public WalletResponse withdraw(@Valid @RequestBody AmountRequest request){
        WalletResponse wallet = walletService.getWalletByUserId(CurrentUser.id());
        return walletService.withdraw(wallet.getId(), request.getAmount());
    }

    @GetMapping("/me/transactions")
    public List<TransactionResponse> getMyTransactions(){
        WalletResponse wallet = walletService.getWalletByUserId(CurrentUser.id());
        return walletService.getTransactions(wallet.getId());
    }

    @GetMapping("/user/{userId}")
    public WalletResponse getWalletByUser(@PathVariable Long userId){
        return walletService.getWalletByUserId(userId);
    }

    @PostMapping("/{walletId}/pay")
    public PaymentResponse pay(@PathVariable Long walletId, @Valid @RequestBody PaymentRequest request){
        return walletService.pay(walletId,request);
    }
}
