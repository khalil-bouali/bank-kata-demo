package com.kbouali.demo.dto.response;

import com.kbouali.demo.util.enums.TransactionType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransactionResponse(Long accountId, int amount, TransactionType transactionType, LocalDateTime dateTime) {
}
