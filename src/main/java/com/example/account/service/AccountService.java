package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
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
  public void createAccount(Long userId, Long initialBalance) {
    AccountUser accountUser = accountUserRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));
  }

  @Transactional
  public Account getAccount(Long id) {
    if(id < 0){
      throw new RuntimeException("Minus");
    }
    return accountRepository.findById(id).get();
  }
}
