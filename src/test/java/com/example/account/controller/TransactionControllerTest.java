package com.example.account.controller;

import static com.example.account.type.TransactionResultType.S;
import static com.example.account.type.TransactionType.USE;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.account.dto.AccountDto;
import com.example.account.dto.CancelBalance;
import com.example.account.dto.TransactionDto;
import com.example.account.dto.UseBalance;
import com.example.account.service.TransactionService;
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


@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
  @MockBean
  private TransactionService transactionService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void successUseBalance() throws Exception {
    //given
    given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .accountNumber("1234567890")
            .transactedAt(LocalDateTime.now())
            .amount(12345L)
            .transactionId("transactionId")
            .transactionResultType(S)
            .build());
    //when
    //then
    mockMvc.perform(post("/transaction/use")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new UseBalance.Request(1L, "2000000000", 1000L)
            )))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andExpect(jsonPath("$.transactionResult").value("S"))
        .andExpect(jsonPath("$.transactionId").value("transactionId"))
        .andExpect(jsonPath("$.amount").value(12345L));
  }

  @Test
  void successCancelBalance() throws Exception {
    //given
    given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .accountNumber("1234567890")
            .transactedAt(LocalDateTime.now())
            .amount(200000000L)
            .transactionId("transactionIdForCancel")
            .transactionResultType(S)
            .build());
    //when
    //then
    mockMvc.perform(post("/transaction/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new CancelBalance.Request("transactionId", "2000000000", 1000L)
            )))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andExpect(jsonPath("$.transactionResult").value("S"))
        .andExpect(jsonPath("$.transactionId").value("transactionIdForCancel"))
        .andExpect(jsonPath("$.amount").value(200000000L));
  }

  @Test
  void successQueryTransaction() throws Exception {
    //given

    given(transactionService.queryTransaction(anyString()))
        .willReturn(TransactionDto.builder()
            .accountNumber("1234567890")
            .transactionType(USE)
            .transactedAt(LocalDateTime.now())
            .amount(200000000L)
            .transactionId("transactionIdForCancel")
            .transactionResultType(S)
            .build());
    //when
    //then
    mockMvc.perform(get("/transaction/12345"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andExpect(jsonPath("$.transactionType").value("USE"))
        .andExpect(jsonPath("$.transactionResult").value("S"))
        .andExpect(jsonPath("$.transactionId").value("transactionIdForCancel"))
        .andExpect(jsonPath("$.amount").value(200000000L));
  }
}