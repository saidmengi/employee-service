package com.example.employeeservice.service;

import com.example.employeeservice.dto.request.CreateEmployeeRequest;
import com.example.employeeservice.dto.request.UpdateEmployeeRequest;
import com.example.employeeservice.dto.response.CreateEmployeeResponse;
import com.example.employeeservice.exception.AlreadyExistException;
import com.example.employeeservice.exception.NotFoundException;
import com.example.employeeservice.mapper.EmployeeMapper;
import com.example.employeeservice.model.Employee;
import com.example.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.employeeservice.mapper.EmployeeMapper.mapToCreateEmployeeResponse;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final KafkaMessagePublishService kafkaMessagePublishService;

    public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) throws AlreadyExistException {
        Optional<Employee> employeeOptional = employeeRepository.findByEmail(request.getEmail());
        if (employeeOptional.isPresent()) {
            throw new AlreadyExistException("Employee with the same email already exists", 400);
        }

        Employee employee = employeeRepository.save(EmployeeMapper.updateEmployee(request));
        kafkaMessagePublishService.publish(employee);

        return mapToCreateEmployeeResponse(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(UUID employeeId) throws NotFoundException {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found", 404));
    }

    public Employee updateEmployee(UUID employeeId, UpdateEmployeeRequest request) throws NotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found", 404));

        EmployeeMapper.updateEmployee(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);

        kafkaMessagePublishService.publish(updatedEmployee);
        return updatedEmployee;
    }

    public void deleteEmployee(UUID employeeId) {
        employeeRepository.deleteById(employeeId);
        kafkaMessagePublishService.publishDeletion(employeeId);
    }
}
