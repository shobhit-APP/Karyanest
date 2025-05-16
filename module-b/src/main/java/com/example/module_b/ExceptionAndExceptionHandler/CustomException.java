package com.example.module_b.ExceptionAndExceptionHandler;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends RuntimeException {
    private String errorMessage;
    private HttpStatus status;
    public CustomException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
