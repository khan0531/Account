package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
  private final AccountService accountService;

  @PostMapping("/account")
  public CreateAccount.Response createAccount(
      @RequestBody @Valid CreateAccount.Request request) {
    return CreateAccount.Response.from(
        accountService.createAccount(
          request.getUserId(),
          request.getInitialBalance()
        ));
  }

  @DeleteMapping("/account")
  public DeleteAccount.Response createAccount(
      @RequestBody @Valid DeleteAccount.Request request) {
    return DeleteAccount.Response.from(
        accountService.deleteAccount(
            request.getUserId(),
            request.getAccountNumber()
        ));
  }

  @GetMapping("/account/{id}")
  public Account getAccount(@PathVariable Long id) {
    return accountService.getAccount(id);
  }
}
