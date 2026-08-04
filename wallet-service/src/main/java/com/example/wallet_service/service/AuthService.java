package com.example.wallet_service.service;


import com.example.wallet_service.dto.*;
import com.example.wallet_service.entity.User;
import com.example.wallet_service.entity.Wallet;
import com.example.wallet_service.exception.AuthException;
import com.example.wallet_service.repository.UserRepository;
import com.example.wallet_service.repository.WalletRepository;
import com.example.wallet_service.security.CurrentUser;
import com.example.wallet_service.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthService(UserRepository userRepository, WalletRepository walletRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new AuthException("Username already taken");
        }
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AuthException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();
        user = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .userId(user.getId())
                .balance(BigDecimal.ZERO)
                .currency("EGP")
                .build();
        wallet = walletRepository.save(wallet);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .walletId(wallet.getId())
                .build();
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new AuthException("Invalid email or password");
        }

        Wallet wallet = walletRepository.findByUserId(user.getId()).orElse(null);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return  AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .walletId(wallet == null ? null : wallet.getId())
                .build();
    }

    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request){
        User user = userRepository.findById(CurrentUser.id())
                .orElseThrow(() -> new AuthException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AuthException("Old password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AuthException("New password must be different from the old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AuthException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ChangePasswordResponse.builder()
                .status("SUCCESS")
                .message("Password changed successfully")
                .build();
    }

    @Transactional
    public ChangeUsernameResponse changeUsername(ChangeUsernameRequest request){
        User user = userRepository.findById(CurrentUser.id())
                .orElseThrow(() -> new AuthException("User not found"));

        user.setUsername(request.getUsername());
        userRepository.save(user);

        return ChangeUsernameResponse.builder()
                .status("SUCCESS")
                .message("Username changed successfully")
                .build();
    }
}
