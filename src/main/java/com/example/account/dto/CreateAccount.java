package com.example.account.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateAccount {

  @Getter
  @Setter
  public static class Request {
    @NotNull
    @Min(1)
    private Long userId;

    @NotNull
    @Min(100)
    private Long initialBalance;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private Long userId;
    private Long accountNumber;
    private LocalDateTime registeredAt;
  }
}
