package com.example.account.domain;

import static com.example.account.type.ErrorCode.AMOUNT_EXCEED_BALANCE;

import com.example.account.exception.AccountException;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import java.time.LocalDateTime;
import lombok.*;

import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class) //이거 안하면 @CreatedDate, @LastModifiedDate 안먹음. JpaAuditingConfiguration에서도 해줘야함
public class Account {
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

  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;

  public void useBalance(Long amount) {
    if (amount > balance) {
      throw new AccountException(AMOUNT_EXCEED_BALANCE);
    }
    balance -= amount;
  }
}
