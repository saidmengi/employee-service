package com.example.employeeservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {
    @Email
    private String email;
    @NotBlank
    private String fullName;

    @DateTimeFormat(pattern = "YYYY-MM-DD)")
    private LocalDate birthday;
    @NotEmpty
    private List<String> hobbies;
}
