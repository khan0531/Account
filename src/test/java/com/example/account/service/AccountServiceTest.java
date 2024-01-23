package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountUserRepository accountUserRepository;

  @InjectMocks
  private AccountService accountService;

  @Test
  void createAccountSuccess() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findFirstByOrderByIdDesc())
        .willReturn(Optional.of(Account.builder()
                .accountUser(user)
            .accountNumber("1000000012").build()));

    given(accountRepository.save(any()))
        .willReturn(Account.builder()
            .accountUser(user)
            .accountNumber("1000000013").build());

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

    //when
    AccountDto accountDto = accountService.createAccount(1L, 1000L);


    //then
    verify(accountRepository, times(1)).save(captor.capture());
    assertEquals(12L, accountDto.getUserId());
    assertEquals("1000000013", captor.getValue().getAccountNumber());
  }

  @Test
  void createFirstAccount() {
    //given
    AccountUser user = AccountUser.builder()
        .id(15L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findFirstByOrderByIdDesc())
        .willReturn(Optional.empty());

    given(accountRepository.save(any()))
        .willReturn(Account.builder()
            .accountUser(user)
            .accountNumber("1000000013").build());

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

    //when
    AccountDto accountDto = accountService.createAccount(1L, 1000L);


    //then
    verify(accountRepository, times(1)).save(captor.capture());
    assertEquals(15L, accountDto.getUserId());
    assertEquals("1000000000", captor.getValue().getAccountNumber());
  }

  @Test
  @DisplayName("해당 유저 음 - 계좌 생성 실패")
  void createAccount_UserNotFound() {
    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.createAccount(1L, 1000L));

    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void createAccount_maxAccountIs10() {
    //given
    AccountUser user = AccountUser.builder()
        .id(15L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.countByAccountUser(any()))
        .willReturn(10);

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.createAccount(1L, 1000L));

    //then
    assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());
  }

  @Test
  void deleteAccountSuccess() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(user)
            .balance(0L)
            .accountNumber("1000000012").build()));

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

    //when
    AccountDto accountDto = accountService.deleteAccount(1L, "1000000000");

    //then
    verify(accountRepository, times(1)).save(captor.capture());
    assertEquals(12L, accountDto.getUserId());
    assertEquals("1000000012", captor.getValue().getAccountNumber());
    assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
  }

  @Test
  @DisplayName("해당 유저 없음 - 계좌 해지 실패")
  void deleteAccount_UserNotFound() {
    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1000000000"));

    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
  void deleteAccount_AccountNotFound() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.empty());

    ///when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1000000000"));

    //then
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("계좌 소유주 다름")
  void deleteAccountFailed_userUnMatch() {
    //given
    AccountUser user1 = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    AccountUser user2 = AccountUser.builder()
        .id(13L)
        .name("user2").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user1));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(user2)
            .balance(0L)
            .accountNumber("1000000012").build()));

    ///when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1000000000"));

    //then
    assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCHED, exception.getErrorCode());
  }

  @Test
  @DisplayName("해지 계좌는 잔액이 없어야 한다.")
  void deleteAccountFailed_balanceNotEmpty() {
    //given
    AccountUser user1 = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user1));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(user1)
            .balance(100L)
            .accountNumber("1000000012").build()));

    ///when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1000000000"));

    //then
    assertEquals(ErrorCode.ACCOUNT_HAS_BALANCE, exception.getErrorCode());
  }

  @Test
  @DisplayName("해지 계좌는 해지할 수 없다.")
  void deleteAccountFailed_alreadyUnRegistered() {
    //given
    AccountUser user1 = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user1));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
            .accountUser(user1)
            .accountStatus(AccountStatus.UNREGISTERED)
            .balance(0L)
            .accountNumber("1000000012").build()));

    ///when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(1L, "1000000000"));

    //then
    assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
  }

  @Test
  void successGetAccountsByUserId() {
    //given
    AccountUser user1 = AccountUser.builder()
        .id(12L)
        .name("user1").build();
    List<Account> accounts = Arrays.asList(
        Account.builder()
            .accountUser(user1)
            .accountNumber("1000000012")
            .balance(1000L)
            .build(),
        Account.builder()
            .accountUser(user1)
            .accountNumber("1000000013")
            .balance(2000L)
            .build(),
        Account.builder()
            .accountUser(user1)
            .accountNumber("1000000014")
            .balance(3000L)
            .build()
    );

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user1));
    given(accountRepository.findByAccountUser(any()))
        .willReturn(accounts);

    //when
    List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

    //then
    assertEquals(3, accountDtos.size());
    assertEquals("1000000012", accountDtos.get(0).getAccountNumber());
    assertEquals(1000, accountDtos.get(0).getBalance());
    assertEquals("1000000013", accountDtos.get(1).getAccountNumber());
    assertEquals(2000, accountDtos.get(1).getBalance());
    assertEquals("1000000014", accountDtos.get(2).getAccountNumber());
    assertEquals(3000, accountDtos.get(2).getBalance());
  }

  @Test
  void failedToGetAccountsByUserId_UserNotFound() {
    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> accountService.getAccountsByUserId(1L));

    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
  }
}