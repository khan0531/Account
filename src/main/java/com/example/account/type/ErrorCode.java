package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  USER_NOT_FOUND("사용자가 없습니다."),
  MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다."),
  ACCOUNT_NOT_FOUND("계좌가 없습니다."),
  USER_ACCOUNT_UN_MATCHED("사용자와 계좌가 일치하지 않습니다."),
  ACCOUNT_ALREADY_UNREGISTERED("해지된 계좌입니다."),
  ACCOUNT_HAS_BALANCE("잔액이 있는 계좌는 해지할 수 없습니다."),
  AMOUNT_EXCEED_BALANCE("잔액이 부족합니다."),
  TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),
  TRANSACTION_ACCOUNT_UN_MATCHED("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),
  CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),
  TOO_OLD_ORDER_TO_CANCEL("1년이 지난 거래는 취소할 수 없습니다. "),
  INVALID_REQUEST("잘못된 요청입니다.");

  private final String description;
}
