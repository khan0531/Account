package com.example.account.service;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.AccountStatus.UNREGISTERED;
import static com.example.account.type.ErrorCode.ACCOUNT_ALREADY_UNREGISTERED;
import static com.example.account.type.ErrorCode.ACCOUNT_NOT_FOUND;
import static com.example.account.type.ErrorCode.CANCEL_MUST_FULLY;
import static com.example.account.type.ErrorCode.TOO_OLD_ORDER_TO_CANCEL;
import static com.example.account.type.ErrorCode.TRANSACTION_ACCOUNT_UN_MATCHED;
import static com.example.account.type.ErrorCode.TRANSACTION_NOT_FOUND;
import static com.example.account.type.ErrorCode.USER_ACCOUNT_UN_MATCHED;
import static com.example.account.type.ErrorCode.USER_NOT_FOUND;
import static com.example.account.type.TransactionResultType.F;
import static com.example.account.type.TransactionResultType.S;
import static com.example.account.type.TransactionType.CANCEL;
import static com.example.account.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountUserRepository accountUserRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
    void successUseBalance() {
      //given
      AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));

    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .account(account)
            .amount(1000L)
            .balanceSnapShot(9000L)
            .transactionId("transactionId")
            .transactionResultType(S)
            .transactionType(USE)
            .transactedAt(LocalDateTime.now())
            .build());

    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
    //when
    TransactionDto transactionDto = transactionService.useBalance(12L, "1000000012", 1000L);
    //then
    verify(transactionRepository, times(1)).save(captor.capture());
    assertEquals(1000L, captor.getValue().getAmount());
    assertEquals(9000L, captor.getValue().getBalanceSnapShot());
    assertEquals(S, captor.getValue().getTransactionResultType());
    assertEquals(USE, captor.getValue().getTransactionType());
    assertEquals(9000L, transactionDto.getBalanceSnapShot());
    assertEquals(S, transactionDto.getTransactionResultType());
    assertEquals(USE, transactionDto.getTransactionType());
    assertEquals(1000L, transactionDto.getAmount());
  }

  @Test
  @DisplayName("해당 유저 없음 - 계좌 생성 실패")
  void useBalance_UserNotFound() {
    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());
    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.useBalance(1L, "1000000000", 1000L));

    //then
    assertEquals(USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("해당 계좌 없음 - 잔액 사용 실패")
  void useBalance_AccountNotFound() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.empty());
    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.useBalance(1L, "1000000000", 1000L));

    //then
    assertEquals(ACCOUNT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("계좌 소유주 다름 - 잔액 사용 실패")
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
        () -> transactionService.useBalance(1L, "1000000000", 1000L));


    //then
    assertEquals(USER_ACCOUNT_UN_MATCHED, exception.getErrorCode());
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
            .accountStatus(UNREGISTERED)
            .balance(0L)
            .accountNumber("1000000012").build()));

    ///when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.useBalance(1L, "1000000000", 1000L));

    //then
    assertEquals(ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
  }

  @Test
  @DisplayName("거래 금액이 잔액보다 큰 경우")
  void exceedAmount_UseBalance() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));


    ///when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.useBalance(1L, "1000000000", 1000000L));

    //then
    assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패 트렌젝션 저장 성공")
  void saveFailedUseTransaction() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

//    given(accountUserRepository.findById(anyLong()))
//        .willReturn(Optional.of(user));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));

    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .account(account)
            .amount(1000L)
            .balanceSnapShot(9000L)
            .transactionId("transactionId")
            .transactionResultType(S)
            .transactionType(USE)
            .transactedAt(LocalDateTime.now())
            .build());

    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
    //when
    transactionService.saveFailedUseTransaction("1000000012", 1000L);

    //then
    verify(transactionRepository, times(1)).save(captor.capture());
    assertEquals(1000L, captor.getValue().getAmount());
    assertEquals(10000L, captor.getValue().getBalanceSnapShot());
    assertEquals(F, captor.getValue().getTransactionResultType());
  }

  @Test
  void successCancelTransaction() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

    Transaction transaction = Transaction.builder()
        .account(account)
        .amount(1000L)
        .balanceSnapShot(9000L)
        .transactionId("transactionId")
        .transactionResultType(S)
        .transactionType(USE)
        .transactedAt(LocalDateTime.now())
        .build();

    given(transactionRepository.findByTransactionId(anyString()))
        .willReturn(Optional.of(transaction));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));

    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .account(account)
            .amount(1000L)
            .balanceSnapShot(9000L)
            .transactionId("transactionIdForCancel")
            .transactionResultType(S)
            .transactionType(CANCEL)
            .transactedAt(LocalDateTime.now())
            .build());

    ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
    //when
    TransactionDto transactionDto = transactionService.cancelBalance("transactionId", "1000000012", 1000L);
    //then
    verify(transactionRepository, times(1)).save(captor.capture());
    assertEquals(1000L, captor.getValue().getAmount());
    assertEquals(11000, captor.getValue().getBalanceSnapShot());
    assertEquals(9000, transactionDto.getBalanceSnapShot());
    assertEquals(S, transactionDto.getTransactionResultType());
    assertEquals(CANCEL, transactionDto.getTransactionType());
  }

  @Test
  @DisplayName("해당 계좌 없음 - 잔액 사용 취소 실패")
  void cancelTransaction_AccountNotFound() {
    //given

    given(transactionRepository.findByTransactionId(anyString()))
        .willReturn(Optional.of(Transaction.builder().build()));


    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.empty());
    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

    //then
    assertEquals(ACCOUNT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("해당 거래 없음 - 잔액 사용 취소 실패")
  void cancelTransaction_TransactionNotFound() {
    //given

    given(transactionRepository.findByTransactionId(anyString()))
        .willReturn(Optional.empty());

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

    //then
    assertEquals(TRANSACTION_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("거래와 계좌가 매칭 실패 - 잔액 사용 취소 실패")
  void cancelTransaction_TransactionAccountUnMatch() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .id(1L)
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

    Account accountNotUse = Account.builder()
        .id(2L)
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000013").build();

    Transaction transaction = Transaction.builder()
        .account(account)
        .amount(1000L)
        .balanceSnapShot(9000L)
        .transactionId("transactionId")
        .transactionResultType(S)
        .transactionType(USE)
        .transactedAt(LocalDateTime.now())
        .build();

    given(transactionRepository.findByTransactionId(anyString()))
        .willReturn(Optional.of(transaction));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(accountNotUse));

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

    //then
    assertEquals(TRANSACTION_ACCOUNT_UN_MATCHED, exception.getErrorCode());
  }

  @Test
  @DisplayName("거래와 금액과 취소 금액이 다름 - 잔액 사용 취소 실패")
  void cancelTransaction_CancelMustFully() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .id(1L)
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

    Transaction transaction = Transaction.builder()
        .account(account)
        .amount(2000L)
        .balanceSnapShot(9000L)
        .transactionId("transactionId")
        .transactionResultType(S)
        .transactionType(USE)
        .transactedAt(LocalDateTime.now())
        .build();

    given(transactionRepository.findByTransactionId(anyString()))
        .willReturn(Optional.of(transaction));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

    //then
    assertEquals(CANCEL_MUST_FULLY, exception.getErrorCode());
  }

  @Test
  @DisplayName("1년이 지난 거래 취소 실패 - 잔액 사용 취소 실패")
  void cancelTransaction_TooOldOlder() {
    //given
    AccountUser user = AccountUser.builder()
        .id(12L)
        .name("user1").build();

    Account account = Account.builder()
        .id(1L)
        .accountUser(user)
        .accountStatus(IN_USE)
        .balance(10000L)
        .accountNumber("1000000012").build();

    Transaction transaction = Transaction.builder()
        .account(account)
        .amount(1000L)
        .balanceSnapShot(9000L)
        .transactionId("transactionId")
        .transactionResultType(S)
        .transactionType(USE)
        .transactedAt(LocalDateTime.now().minusYears(1))
        .build();

    given(transactionRepository.findByTransactionId(anyString()))
        .willReturn(Optional.of(transaction));

    given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(account));

    //when
    AccountException exception = assertThrows(AccountException.class,
        () -> transactionService.cancelBalance("transactionId", "1000000000", 1000L));

    //then
    assertEquals(TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
  }
}