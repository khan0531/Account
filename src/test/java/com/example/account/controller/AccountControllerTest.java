package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount.Request;
import com.example.account.dto.DeleteAccount;
import com.example.account.type.AccountStatus;
import com.example.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
  @MockBean
  private AccountService accountService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void successCreateAccount() throws Exception {
    //given
    given(accountService.createAccount(anyLong(), anyLong()))
        .willReturn(AccountDto.builder()
            .userId(1L)
            .accountNumber("1234567890")
            .registeredAt(LocalDateTime.now())
            .unRegisteredAt(LocalDateTime.now())
            .build());
    //when

    //then
    mockMvc.perform(post("/account")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
            new Request(1L, 1000L)
        )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andDo(print());
  }

  @Test
  void successDeleteAccount() throws Exception {
    //given
    given(accountService.deleteAccount(anyLong(), anyString()))
        .willReturn(AccountDto.builder()
            .userId(1L)
            .accountNumber("1234567890")
            .registeredAt(LocalDateTime.now())
            .unRegisteredAt(LocalDateTime.now())
            .build());
    //when

    //then
    mockMvc.perform(delete("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new DeleteAccount.Request(1L, "1111111111")
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andDo(print());
  }

  @Test
  void successGetAccountsByUserId() throws Exception {
    //given
    List<AccountDto> accountDtos =
        Arrays.asList(
            AccountDto.builder()
                .accountNumber("1234567890")
                .balance(1000L)
                .registeredAt(LocalDateTime.now())
                .unRegisteredAt(LocalDateTime.now())
                .build(),
            AccountDto.builder()
                .accountNumber("1111111111")
                .balance(2000L)
                .registeredAt(LocalDateTime.now())
                .unRegisteredAt(LocalDateTime.now())
                .build(),
            AccountDto.builder()
                .accountNumber("2222222222")
                .balance(3000L)
                .registeredAt(LocalDateTime.now())
                .unRegisteredAt(LocalDateTime.now())
                .build()
        );
    given(accountService.getAccountsByUserId(anyLong()))
        .willReturn(accountDtos);
    //when
    //then
    mockMvc.perform(get("/account?user_id=1"))
        .andDo(print())
        .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
        .andExpect(jsonPath("$[0].balance").value(1000L))
        .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
        .andExpect(jsonPath("$[1].balance").value(2000L))
        .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
        .andExpect(jsonPath("$[2].balance").value(3000L))
        .andExpect(status().isOk());
  }
}