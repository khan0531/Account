package com.example.account.controller;

import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/transaction/use")
  public UseBalance.Response useBalance(UseBalance.Request request) {
    try {
      return UseBalance.Response.from(transactionService.useBalance(request.getUserId(), request.getAccountNumber(), request.getAmount()));
    } catch (AccountException e) {
      log.error("잔액 사용 중 에러 발생", e);

      transactionService.saveFailedUseTransaction(
          request.getAccountNumber(),
          request.getAmount()
      );

      throw e;
    }
  }
}
