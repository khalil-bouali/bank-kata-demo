package com.kbouali.demo.entity;

import com.kbouali.demo.util.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @NonNull
    private LocalDateTime dateTime;
    @NonNull
    private Integer amount;
    @NonNull
    private Integer balance;
    @ManyToOne
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountEntity account;
}
