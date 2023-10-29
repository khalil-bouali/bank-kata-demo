package com.kbouali.demo.service;

import com.kbouali.demo.dto.request.ChangePasswordRequest;
import com.kbouali.demo.dto.request.TransactionRequest;
import com.kbouali.demo.dto.response.TransactionResponse;

import java.security.Principal;

public interface AccountService {

    TransactionResponse deposit(TransactionRequest request);
    TransactionResponse withdraw(TransactionRequest request);
    String printStatement(Long accountId);
    void changePassword(ChangePasswordRequest request, Principal connectedUser);
}
