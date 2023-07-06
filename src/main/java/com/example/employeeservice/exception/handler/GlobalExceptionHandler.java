package com.example.employeeservice.exception.handler;

import com.example.employeeservice.exception.AlreadyExistException;
import com.example.employeeservice.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> handle(NotFoundException exception) {

        var message = getMessage(exception.getMessage(), (Object) exception.getArgs());
        var error = ErrorInfo.builder()
                .errorMessage(message)
                .errorCode(String.valueOf(exception.getErrorCode()))
                .build();

        log.error(error.getErrorMessage(), error);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorInfo> handle(AlreadyExistException exception) {

        var message = getMessage(exception.getMessage(), (Object) exception.getArgs());
        var error = ErrorInfo.builder()
                .errorMessage(message)
                .errorCode(String.valueOf(exception.getErrorCode()))
                .build();

        log.error(error.getErrorMessage(), error);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleConstraintViolationException(BindException e) {
        if (e.getBindingResult().hasFieldErrors()) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream().map(
                    f -> new FieldError(
                            f.getField(),
                            getMessage(f.getDefaultMessage())
                    )
            ).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(fieldErrors);
        }
        return ResponseEntity.badRequest().body("entity.invalid");
    }

    public record FieldError(String field, String error) {
    }

    private String getMessage(String exceptionMessage, Object... args) {
        return MessageFormat.format(exceptionMessage, args);
    }
}
