package com.example.account.controller;

import com.example.account.aop.AccountLock;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.QueryTransactionResponse;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
  @AccountLock
  public UseBalance.Response useBalance(@RequestBody @Valid UseBalance.Request request) {
    TransactionDto transactionDto = transactionService.useBalance(request.getUserId(), request.getAccountNumber(),
        request.getAmount());
    try {
      return UseBalance.Response.from(transactionDto);
    } catch (AccountException e) {
      log.error("잔액 사용 중 에러 발생", e);

      transactionService.saveFailedUseTransaction(
          request.getAccountNumber(),
          request.getAmount()
      );

      throw e;
    }
  }

  @PostMapping("/transaction/cancel")
  @AccountLock
  public CancelBalance.Response useBalance(@RequestBody @Valid CancelBalance.Request request) {
    TransactionDto transactionDto = transactionService.cancelBalance(request.getTransactionId(), request.getAccountNumber(),
        request.getAmount());
    try {
      return CancelBalance.Response.from(transactionDto);
    } catch (AccountException e) {
      log.error("잔액 사용 중 에러 발생", e);

      transactionService.saveFailedCancelTransaction(
          request.getAccountNumber(),
          request.getAmount()
      );

      throw e;
    }
  }

  @GetMapping("/transaction/{transactionId}")
  public QueryTransactionResponse queryTransaction(@PathVariable String transactionId) {
    return QueryTransactionResponse.from(transactionService.queryTransaction(transactionId));
  }
}
