package com.example.account.domain;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;

import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass // 이거 안하면 BaseEntity를 상속받은 Account에서 컬럼이 안생김
@EntityListeners(AuditingEntityListener.class) //이거 안하면 @CreatedDate, @LastModifiedDate 안먹음.
public class BaseEntity {

  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
