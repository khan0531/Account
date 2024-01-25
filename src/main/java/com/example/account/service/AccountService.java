package com.example.account.service;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.AccountStatus.UNREGISTERED;
import static com.example.account.type.ErrorCode.ACCOUNT_ALREADY_UNREGISTERED;
import static com.example.account.type.ErrorCode.ACCOUNT_HAS_BALANCE;
import static com.example.account.type.ErrorCode.ACCOUNT_NOT_FOUND;
import static com.example.account.type.ErrorCode.USER_ACCOUNT_UN_MATCHED;
import static com.example.account.type.ErrorCode.USER_NOT_FOUND;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
    AccountUser accountUser = getAccountUser(userId);

    validateCreateAccount(accountUser);

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

  private AccountUser getAccountUser(Long userId) {
    AccountUser accountUser = accountUserRepository.findById(userId)
        .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    return accountUser;
  }

  private void validateCreateAccount(AccountUser accountUser) {
    if (accountRepository.countByAccountUser(accountUser) >= 10) {
      throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
    }
  }

  @Transactional
  public Account getAccount(Long id) {
    if(id < 0){
      throw new RuntimeException("Minus");
    }
    return accountRepository.findById(id).get();
  }

  @Transactional
  public AccountDto deleteAccount(Long userId, String accountNumber) {
    AccountUser accountUser = getAccountUser(userId);
    Account account = accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

    validateDeleteAccount(accountUser, account);

    account.setAccountStatus(UNREGISTERED);
    account.setUnRegisteredAt(LocalDateTime.now());

    accountRepository.save(account); //테스트 코드를 위해 추가

    return AccountDto.fromEntity(account);
  }

  private void validateDeleteAccount(AccountUser accountUser, Account account) throws AccountException {
    if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())){
      throw new AccountException(USER_ACCOUNT_UN_MATCHED);
    }
    if (account.getAccountStatus() == UNREGISTERED) {
      throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
    }
    if (account.getBalance() > 0) {
      throw new AccountException(ACCOUNT_HAS_BALANCE);
    }
  }

  @Transactional
  public List<AccountDto> getAccountsByUserId(Long userId) {
    AccountUser accountUser = getAccountUser(userId);

    List<Account> accounts = accountRepository
        .findByAccountUser(accountUser);

    return accounts.stream()
        .map(AccountDto::fromEntity)
        .collect(Collectors.toList());
  }
}
