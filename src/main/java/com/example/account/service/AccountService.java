package com.example.account.service;

import static com.example.account.type.AccountStatus.IN_USE;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final AccountRepository accountRepository;
  private final AccountUserRepository accountUserRepository;

  /**
   * 사용자가 있는지 조회
   * 계좌의 번호 생성
   * 계좌를 저장하고, 그 정보를 넘긴다.
   */
  @Transactional
  public AccountDto createAccount(Long userId, Long initialBalance) {
    AccountUser accountUser = accountUserRepository.findById(userId)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

    String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
        .map(account -> String.valueOf(Long.parseLong(account.getAccountNumber()) + 1))
        .orElse("1000000000");

    return AccountDto.fromEntity(
        accountRepository.save(
          Account.builder()
              .accountUser(accountUser)
              .accountStatus(IN_USE)
              .accountNumber(newAccountNumber)
              .balance(initialBalance)
              .registeredAt(LocalDateTime.now())
              .build())
    );
  }

  @Transactional
  public Account getAccount(Long id) {
    if(id < 0){
      throw new RuntimeException("Minus");
    }
    return accountRepository.findById(id).get();
  }
}
