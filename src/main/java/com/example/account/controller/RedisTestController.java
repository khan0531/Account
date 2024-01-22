package com.example.account.controller;

import com.example.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisTestController {
  private final RedisTestService redisTestService;

  @GetMapping("/get-lock")
  public String getLock() {
    return redisTestService.getLock();
  }
}
