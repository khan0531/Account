package com.example.account.dto;

import com.example.account.type.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.QueryAnnotation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;
}
