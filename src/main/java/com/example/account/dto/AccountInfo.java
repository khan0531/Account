package com.example.account.dto;

import com.example.account.type.AccountStatus;
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
public class AccountInfo {
  private String accountNumber;
  private Long balance;

  public static AccountInfo from(AccountDto accountDto) {
    return AccountInfo.builder()
        .accountNumber(accountDto.getAccountNumber())
        .balance(accountDto.getBalance())
        .build();
  }
}
