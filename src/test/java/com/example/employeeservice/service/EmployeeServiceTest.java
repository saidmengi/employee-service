package com.example.employeeservice.service;

import com.example.employeeservice.dto.request.CreateEmployeeRequest;
import com.example.employeeservice.dto.request.UpdateEmployeeRequest;
import com.example.employeeservice.dto.response.CreateEmployeeResponse;
import com.example.employeeservice.exception.AlreadyExistException;
import com.example.employeeservice.exception.NotFoundException;
import com.example.employeeservice.model.Employee;
import com.example.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KafkaMessagePublishService kafkaMessagePublishService;

    @InjectMocks
    private EmployeeService employeeService;

    @Nested
    @DisplayName("Create Employee Tests")
    class CreateEmployee {
        @Test
        void itShouldCreateEmployee() throws AlreadyExistException {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .email("test@example.com")
                    .fullName("John Doe")
                    .birthday(LocalDate.of(1990, 1, 1))
                    .hobbies(Arrays.asList("Reading", "Gardening"))
                    .build();
            Employee employee = Employee.builder()
                    .id(UUID.randomUUID())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .birthday(request.getBirthday())
                    .hobbies(request.getHobbies())
                    .build();

            when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

            // Act
            CreateEmployeeResponse response = employeeService.createEmployee(request);

            // Then
            assertNotNull(response.getId());
            assertEquals(request.getEmail(), response.getEmail());
            assertEquals(request.getFullName(), response.getFullName());
            assertEquals(request.getBirthday(), response.getBirthday());
            assertEquals(request.getHobbies(), response.getHobbies());
            verify(employeeRepository).findByEmail(request.getEmail());
            verify(employeeRepository).save(any(Employee.class));
            verify(kafkaMessagePublishService).publish(employee);
        }

        @Test
        void itShouldThrowExceptionWhenEmailIsDuplicated() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .email("test@example.com")
                    .fullName("John Doe")
                    .birthday(LocalDate.of(1990, 1, 1))
                    .hobbies(Arrays.asList("Reading", "Gardening"))
                    .build();
            Employee existingEmployee = Employee.builder()
                    .id(UUID.randomUUID())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .birthday(request.getBirthday())
                    .hobbies(request.getHobbies())
                    .build();

            when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingEmployee));

            // When
            assertThrows(AlreadyExistException.class, () -> employeeService.createEmployee(request));

            // Then
            verify(employeeRepository).findByEmail(request.getEmail());
            verify(employeeRepository, never()).save(any(Employee.class));
            verify(kafkaMessagePublishService, never()).publish(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("Get All Employees Tests")
    class GetAllEmployees {
        @Test
        void getAllEmployees() {
            // Given
            List<Employee> employees = Arrays.asList(
                    Employee.builder().id(UUID.randomUUID()).build(),
                    Employee.builder().id(UUID.randomUUID()).build()
            );

            when(employeeRepository.findAll()).thenReturn(employees);

            // Act
            List<Employee> result = employeeService.getAllEmployees();

            // Then
            assertEquals(employees, result);
            verify(employeeRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Get Employee By Id Tests")
    class GetEmployeeById {
        @Test
        void itShouldGetEmployeeById() throws NotFoundException {
            // Given
            UUID employeeId = UUID.randomUUID();
            Employee employee = Employee.builder()
                    .id(employeeId)
                    .email("test@example.com")
                    .fullName("John Doe")
                    .birthday(LocalDate.of(1990, 1, 1))
                    .hobbies(Arrays.asList("Reading", "Gardening"))
                    .build();

            when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

            // Act
            Employee result = employeeService.getEmployeeById(employeeId);

            // Then
            assertEquals(employee, result);
            verify(employeeRepository).findById(employeeId);
        }

        @Test
        void itShouldThrowExceptionWhenEmployeeNotFound() {
            // Given
            UUID nonExistingId = UUID.randomUUID();

            when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

            // When
            assertThrows(NotFoundException.class, () -> employeeService.getEmployeeById(nonExistingId));

            // Then
            verify(employeeRepository).findById(nonExistingId);
        }
    }

    @Nested
    @DisplayName("Update Employee Tests")
    class UpdateEmployee {
        @Test
        void itShouldUpdateEmployee() throws NotFoundException {
            // Given
            UUID employeeId = UUID.randomUUID();
            UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                    .email("newemail@example.com")
                    .fullName("Updated Name")
                    .birthday(LocalDate.of(1992, 2, 2))
                    .hobbies(Arrays.asList("Reading", "Swimming"))
                    .build();
            Employee existingEmployee = Employee.builder()
                    .id(employeeId)
                    .email("oldemail@example.com")
                    .fullName("Old Name")
                    .birthday(LocalDate.of(1990, 1, 1))
                    .hobbies(Arrays.asList("Reading", "Gardening"))
                    .build();

            when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
            when(employeeRepository.save(any(Employee.class))).thenReturn(existingEmployee);

            // Act
            Employee result = employeeService.updateEmployee(employeeId, request);

            // Then
            assertEquals(existingEmployee, result);
            assertEquals(request.getEmail(), result.getEmail());
            assertEquals(request.getFullName(), result.getFullName());
            assertEquals(request.getBirthday(), result.getBirthday());
            assertEquals(request.getHobbies(), result.getHobbies());
            verify(employeeRepository).findById(employeeId);
            verify(kafkaMessagePublishService).publish(existingEmployee);
        }

        @Test
        void itShouldThrowExceptıonWhenEmployeeNotFound() {
            // Given
            UUID nonExistingId = UUID.randomUUID();
            UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                    .email("newemail@example.com")
                    .fullName("Updated Name")
                    .birthday(LocalDate.of(1992, 2, 2))
                    .hobbies(Arrays.asList("Reading", "Swimming"))
                    .build();

            when(employeeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

            // WHen
            assertThrows(NotFoundException.class, () -> employeeService.updateEmployee(nonExistingId, request));

            //Then
            verify(employeeRepository).findById(nonExistingId);
            verify(employeeRepository, never()).save(any(Employee.class));
            verify(kafkaMessagePublishService, never()).publish(any(Employee.class));
        }
    }

    @Test
    void deleteEmployee() {
        // Given
        UUID employeeId = UUID.randomUUID();

        // When
        employeeService.deleteEmployee(employeeId);

        // Then
        verify(employeeRepository).deleteById(employeeId);
        verify(kafkaMessagePublishService).publishDeletion(employeeId);
    }
}
