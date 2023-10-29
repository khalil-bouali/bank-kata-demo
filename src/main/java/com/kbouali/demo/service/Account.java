package com.kbouali.demo.service;

import com.kbouali.demo.dto.request.ChangePasswordRequest;
import com.kbouali.demo.dto.request.TransactionRequest;
import com.kbouali.demo.dto.response.TransactionResponse;
import com.kbouali.demo.entity.AccountEntity;
import com.kbouali.demo.entity.Transaction;
import com.kbouali.demo.repository.AccountRepository;
import com.kbouali.demo.util.enums.TransactionType;
import com.kbouali.demo.util.exception.AccountNotFoundException;
import com.kbouali.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static com.kbouali.demo.util.enums.TransactionType.DEPOSIT;
import static com.kbouali.demo.util.enums.TransactionType.WITHDRAWAL;

@Service
@RequiredArgsConstructor
public class Account implements AccountService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TransactionResponse deposit(TransactionRequest request) {
        return transact(request, DEPOSIT);
    }

    @Override
    public TransactionResponse withdraw(TransactionRequest request) {
        return transact(request, WITHDRAWAL);
    }

    @Override
    public String printStatement(Long accountId) {
        if (accountRepository.findById(accountId).isEmpty()) {
            throw new AccountNotFoundException("Account with id: " + accountId + " not found.");
        }
        List<Transaction> transactions = transactionRepository.findSortedTransactionsByAccount(accountId);
        List<String> prints = transactions.stream()
                .map(this::formatTransaction)
                .toList();
        StringBuilder builder = new StringBuilder();
        builder.append("Date || Amount || Balance\n");
        for (String print : prints) {
            builder.append(print).append("\n");
        }
        return builder.toString();
    }

    private TransactionResponse transact(TransactionRequest request, TransactionType type) {
        Long accountId = request.accountId();
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id: " + accountId + " not found."));

        int amount = request.amount();
        int currentBalance = transactionRepository.findLatestBalanceByAccount(accountId)
                .orElse(0);
        int newBalance = WITHDRAWAL.name().equals(type.name()) ? currentBalance - amount : currentBalance + amount;

        LocalDateTime dateTime = LocalDateTime.now();

        Transaction transaction = Transaction.builder()
                .transactionType(type)
                .amount(amount)
                .balance(newBalance)
                .dateTime(dateTime)
                .account(account)
                .build();
        transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionType(type)
                .dateTime(dateTime)
                .build();
    }

    private String formatTransaction(Transaction transaction) {
        LocalDateTime dateTime = transaction.getDateTime();
        String date = dateTime.getDayOfMonth()
                + "/"
                + dateTime.getMonthValue()
                + "/"
                + dateTime.getYear();
        return date
                + " || "
                + (transaction.getTransactionType().name().equals(WITHDRAWAL.name())
                ? "-" + transaction.getAmount()
                : "" + transaction.getAmount())
                + " || "
                + transaction.getBalance();
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        AccountEntity account = (AccountEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new IllegalStateException("Wrong password.");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords are not the same.");
        }
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }
}
