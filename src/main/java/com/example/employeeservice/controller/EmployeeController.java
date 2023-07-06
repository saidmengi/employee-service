package com.example.employeeservice.controller;

import com.example.employeeservice.dto.request.CreateEmployeeRequest;
import com.example.employeeservice.dto.request.UpdateEmployeeRequest;
import com.example.employeeservice.dto.response.CreateEmployeeResponse;
import com.example.employeeservice.exception.AlreadyExistException;
import com.example.employeeservice.exception.NotFoundException;
import com.example.employeeservice.model.Employee;
import com.example.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Create an employee",
            description = "Creates a new employee with the provided details.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) throws AlreadyExistException {
        return employeeService.createEmployee(request);
    }

    @Operation(
            summary = "Get all employees",
            description = "Retrieves a list of all employees.")
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Operation(
            summary = "Get an employee by ID",
            description = "Retrieves an employee based on the provided employee ID.")
    @GetMapping("/{employeeId}")
    public Employee getEmployeeById(@PathVariable UUID employeeId) throws NotFoundException {
        return employeeService.getEmployeeById(employeeId);
    }

    @Operation(
            summary = "Update an employee",
            description = "Updates the details of an existing employee.")
    @PutMapping("/{employeeId}")
    public Employee updateEmployee(
            @PathVariable UUID employeeId,
            @RequestBody UpdateEmployeeRequest request
    ) throws NotFoundException {
        return employeeService.updateEmployee(employeeId, request);
    }

    @Operation(
            summary = "Delete an employee",
            description = "Deletes an employee based on the provided employee ID.")
    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable UUID employeeId) {
        employeeService.deleteEmployee(employeeId);
    }
}
