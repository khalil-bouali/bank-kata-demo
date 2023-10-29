package com.kbouali.demo.controller;

import com.kbouali.demo.dto.request.ChangePasswordRequest;
import com.kbouali.demo.dto.request.TransactionRequest;
import com.kbouali.demo.dto.response.TransactionResponse;
import com.kbouali.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;


@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(service.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(service.withdraw(request));
    }

    @GetMapping("/print/{accountId}")
    public ResponseEntity<String> printStatement(@PathVariable Long accountId) {
        return ResponseEntity.ok(service.printStatement(accountId));
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request , connectedUser);
        return ResponseEntity.ok().build();
    }
}
