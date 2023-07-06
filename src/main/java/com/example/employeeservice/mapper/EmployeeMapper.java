package com.example.employeeservice.mapper;

import com.example.employeeservice.dto.request.CreateEmployeeRequest;
import com.example.employeeservice.dto.request.UpdateEmployeeRequest;
import com.example.employeeservice.dto.response.CreateEmployeeResponse;
import com.example.employeeservice.model.Employee;

import java.util.UUID;

public class EmployeeMapper {
    public static Employee updateEmployee(CreateEmployeeRequest request) {
        return Employee.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .birthday(request.getBirthday())
                .hobbies(request.getHobbies())
                .build();
    }

    public static Employee updateEmployee(UpdateEmployeeRequest request, Employee employee) {
        employee.setEmail(request.getEmail());
        employee.setFullName(request.getFullName());
        employee.setBirthday(request.getBirthday());
        employee.setHobbies(request.getHobbies());
        return employee;
    }

    public static CreateEmployeeResponse mapToCreateEmployeeResponse(Employee employee) {
        return CreateEmployeeResponse.builder()
                .id(employee.getId())
                .birthday(employee.getBirthday())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .hobbies(employee.getHobbies())
                .build();
    }
}
