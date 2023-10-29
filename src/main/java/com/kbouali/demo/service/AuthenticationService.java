package com.kbouali.demo.service;

import com.kbouali.demo.dto.request.AuthenticationRequest;
import com.kbouali.demo.dto.request.RegisterRequest;
import com.kbouali.demo.dto.response.AuthenticationResponse;
import com.kbouali.demo.entity.AccountEntity;
import com.kbouali.demo.entity.Token;
import com.kbouali.demo.repository.TokenRepository;
import com.kbouali.demo.util.enums.TokenType;
import com.kbouali.demo.repository.AccountRepository;
import com.kbouali.demo.util.exception.AccountNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kbouali.demo.util.enums.Role.USER;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        AccountEntity accountEntity = AccountEntity.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .cin(request.getCin())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : USER)
                .build();
        AccountEntity savedAccountEntity = accountRepository.save(accountEntity);
        String jwtToken = jwtService.generateToken(accountEntity);
        String refreshToken = jwtService.generateRefreshToken(accountEntity);
        saveUserToken(savedAccountEntity, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCIN(),
                        request.getPassword()
                )
        );
        AccountEntity accountEntity = accountRepository.findByCin(request.getCIN())
                .orElseThrow(() -> new AccountNotFoundException("Account with CIN: " + request.getCIN() + " not found."));
        String jwtToken = jwtService.generateToken(accountEntity);
        String refreshToken = jwtService.generateRefreshToken(accountEntity);
        revokeAllUserTokens(accountEntity);
        saveUserToken(accountEntity, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeAllUserTokens(AccountEntity accountEntity) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(accountEntity.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(AccountEntity accountEntity, String jwtToken) {
        Token token = Token.builder()
                .account(accountEntity)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        final String refreshToken = authHeader.substring(7);
        final String CIN = jwtService.extractCIN(refreshToken);
        if (CIN != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AccountEntity accountEntity = accountRepository.findByCin(CIN)
                    .orElseThrow(() -> new AccountNotFoundException("Account with CIN: " + CIN + " not found."));
            if (jwtService.isTokenValid(refreshToken, accountEntity)) {
                String accessToken = jwtService.generateToken(accountEntity);
                revokeAllUserTokens(accountEntity);
                saveUserToken(accountEntity, accessToken);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        return null;
    }
}
