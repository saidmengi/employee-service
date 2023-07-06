package com.example.employeeservice.service;

import com.example.employeeservice.dto.request.CreateEmployeeRequest;
import com.example.employeeservice.dto.request.UpdateEmployeeRequest;
import com.example.employeeservice.dto.response.CreateEmployeeResponse;
import com.example.employeeservice.exception.AlreadyExistException;
import com.example.employeeservice.exception.NotFoundException;
import com.example.employeeservice.model.Employee;
import com.example.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
public class EmployeeServiceIntegrationTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void mongoDBProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    public void testCreateEmployee() throws AlreadyExistException {
        // Given
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .email("test@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Collections.singletonList("Reading"))
                .build();

        // When
        CreateEmployeeResponse response = employeeService.createEmployee(request);

        // Then
        assertNotNull(response.getId());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getFullName(), response.getFullName());
        assertEquals(request.getBirthday(), response.getBirthday());
        assertEquals(request.getHobbies(), response.getHobbies());
    }

    @Test
    public void testGetAllEmployees() {
        // Given
        employeeRepository.save(Employee.builder()
                .id(UUID.randomUUID())
                .email("test1@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Collections.singletonList("Reading"))
                .build());
        employeeRepository.save(Employee.builder()
                .id(UUID.randomUUID())
                .email("test2@example.com")
                .fullName("Jane Smith")
                .birthday(LocalDate.of(1995, 2, 2))
                .hobbies(Collections.singletonList("Swimming"))
                .build());

        // When
        List<Employee> employees = employeeService.getAllEmployees();

        // Then
        assertEquals(2, employees.size());
    }

    @Test
    public void testGetEmployeeById() throws NotFoundException {
        // Given
        UUID employeeId = UUID.randomUUID();
        Employee employee = Employee.builder()
                .id(employeeId)
                .email("test@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Collections.singletonList("Reading"))
                .build();
        employeeRepository.save(employee);

        // When
        Employee retrievedEmployee = employeeService.getEmployeeById(employeeId);

        // Then
        assertEquals(employee.getId(), retrievedEmployee.getId());
        assertEquals(employee.getEmail(), retrievedEmployee.getEmail());
        assertEquals(employee.getFullName(), retrievedEmployee.getFullName());
        assertEquals(employee.getBirthday(), retrievedEmployee.getBirthday());
        assertEquals(employee.getHobbies(), retrievedEmployee.getHobbies());
    }

    @Test
    public void testUpdateEmployee() throws NotFoundException {
        // Given
        UUID employeeId = UUID.randomUUID();
        Employee employee = Employee.builder()
                .id(employeeId)
                .email("test@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Collections.singletonList("Reading"))
                .build();
        employeeRepository.save(employee);

        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                .email("updated@example.com")
                .fullName("Updated Name")
                .birthday(LocalDate.of(1995, 2, 2))
                .hobbies(Collections.singletonList("Swimming"))
                .build();

        // When
        Employee updatedEmployee = employeeService.updateEmployee(employeeId, request);

        // Then
        assertEquals(request.getEmail(), updatedEmployee.getEmail());
        assertEquals(request.getFullName(), updatedEmployee.getFullName());
        assertEquals(request.getBirthday(), updatedEmployee.getBirthday());
        assertEquals(request.getHobbies(), updatedEmployee.getHobbies());
    }

    @Test
    public void testDeleteEmployee() {
        // Given
        UUID employeeId = UUID.randomUUID();
        Employee employee = Employee.builder()
                .id(employeeId)
                .email("test@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Collections.singletonList("Reading"))
                .build();
        employeeRepository.save(employee);

        // When
        employeeService.deleteEmployee(employeeId);

        // Then
        assertFalse(employeeRepository.existsById(employeeId));
    }
}
