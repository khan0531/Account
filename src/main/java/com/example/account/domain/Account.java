package com.example.account.domain;

import static com.example.account.type.ErrorCode.AMOUNT_EXCEED_BALANCE;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private AccountUser accountUser; //시스템의 유저테이블이랑 겹칠까봐 이렇게 함

  private String accountNumber;

  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;

  private Long balance;

  private LocalDateTime registeredAt;
  private LocalDateTime unRegisteredAt;

  public void useBalance(Long amount) {
    if (amount > balance) {
      throw new AccountException(AMOUNT_EXCEED_BALANCE);
    }
    balance -= amount;
  }

  public void cancelBalance(Long amount) {
    if (amount < 0) {
      throw new AccountException(ErrorCode.INVALID_REQUEST);
    }
    balance += amount;
  }
}
