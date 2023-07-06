package com.example.employeeservice.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


class CreateEmployeeRequestTest {

    private Validator validator;

    @BeforeEach
    public void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidEmail() {
        CreateEmployeeRequest createEmployeeRequest = CreateEmployeeRequest.builder()
                .email("test@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.now())
                .hobbies(List.of("book"))
                .build();

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(createEmployeeRequest);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmail() {
        CreateEmployeeRequest createEmployeeRequest = CreateEmployeeRequest.builder()
                .email("invalidEmail")
                .fullName("John Doe")
                .birthday(LocalDate.now())
                .hobbies(List.of("book"))
                .build();

        Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(createEmployeeRequest);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
    }
}