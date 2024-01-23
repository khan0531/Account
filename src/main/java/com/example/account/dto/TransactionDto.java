package com.example.account.dto;


import com.example.account.domain.Account;
import com.example.account.domain.Transaction;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
  private String accountNumber;
  private TransactionType transactionType;
  private TransactionResultType transactionResultType;
  private Long amount;
  private Long balanceSnapShot;
  private String transactionId;
  private LocalDateTime transactedAt;

  public static TransactionDto fromEntity(Transaction transaction) {
    return TransactionDto.builder()
        .accountNumber(transaction.getAccount().getAccountNumber())
        .transactionType(transaction.getTransactionType())
        .transactionResultType(transaction.getTransactionResultType())
        .amount(transaction.getAmount())
        .balanceSnapShot(transaction.getBalanceSnapShot())
        .transactionId(transaction.getTransactionId())
        .transactedAt(transaction.getTransactedAt())
        .build();
  }
}
