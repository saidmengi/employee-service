package com.example.employeeservice.controller;

import com.example.employeeservice.dto.request.CreateEmployeeRequest;
import com.example.employeeservice.dto.request.UpdateEmployeeRequest;
import com.example.employeeservice.dto.response.CreateEmployeeResponse;
import com.example.employeeservice.model.Employee;
import com.example.employeeservice.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateEmployee() throws Exception {
        // given
        CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                .email("test@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Arrays.asList("Reading", "Sports"))
                .build();

        CreateEmployeeResponse response = CreateEmployeeResponse.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .birthday(request.getBirthday())
                .hobbies(request.getHobbies())
                .build();

        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(response);

        // when then
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.fullName").value(request.getFullName()))
                .andExpect(jsonPath("$.birthday").value(request.getBirthday().toString()))
                .andExpect(jsonPath("$.hobbies").isArray())
                .andExpect(jsonPath("$.hobbies", hasSize(request.getHobbies().size())));
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        // given
        List<Employee> employees = Arrays.asList(
                new Employee(UUID.randomUUID(), "test1@example.com", "John Doe", LocalDate.of(1990, 1, 1), Arrays.asList("Reading", "Sports")),
                new Employee(UUID.randomUUID(), "test2@example.com", "Jane Smith", LocalDate.of(1995, 2, 2), Arrays.asList("Music", "Movies"))
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        // when then
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(employees.size())))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].email").value(employees.get(0).getEmail()))
                .andExpect(jsonPath("$[0].fullName").value(employees.get(0).getFullName()))
                .andExpect(jsonPath("$[0].birthday").value(employees.get(0).getBirthday().toString()))
                .andExpect(jsonPath("$[0].hobbies").isArray())
                .andExpect(jsonPath("$[0].hobbies", hasSize(employees.get(0).getHobbies().size())))
                .andExpect(jsonPath("$[1].id").isNotEmpty())
                .andExpect(jsonPath("$[1].email").value(employees.get(1).getEmail()))
                .andExpect(jsonPath("$[1].fullName").value(employees.get(1).getFullName()))
                .andExpect(jsonPath("$[1].birthday").value(employees.get(1).getBirthday().toString()))
                .andExpect(jsonPath("$[1].hobbies").isArray())
                .andExpect(jsonPath("$[1].hobbies", hasSize(employees.get(1).getHobbies().size())));
    }

    @Test
    public void testGetEmployeeById() throws Exception {
        // given
        UUID employeeId = UUID.randomUUID();

        Employee employee = new Employee(employeeId, "test@example.com", "John Doe", LocalDate.of(1990, 1, 1), Arrays.asList("Reading", "Sports"));


        when(employeeService.getEmployeeById(eq(employeeId))).thenReturn(employee);

        // when then
        mockMvc.perform(get("/employees/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee.getId().toString()))
                .andExpect(jsonPath("$.email").value(employee.getEmail()))
                .andExpect(jsonPath("$.fullName").value(employee.getFullName()))
                .andExpect(jsonPath("$.birthday").value(employee.getBirthday().toString()))
                .andExpect(jsonPath("$.hobbies").isArray())
                .andExpect(jsonPath("$.hobbies", hasSize(employee.getHobbies().size())));
    }

    @Test
    public void testUpdateEmployee() throws Exception {
        // given
        UUID employeeId = UUID.randomUUID();


        UpdateEmployeeRequest request = UpdateEmployeeRequest.builder()
                .email("newemail@example.com")
                .build();

        Employee existingEmployee = Employee.builder()
                .id(employeeId)
                .email("oldemail@example.com")
                .fullName("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .hobbies(Arrays.asList("Reading", "Sports"))
                .build();

        Employee updatedEmployee = Employee.builder()
                .id(employeeId)
                .email(request.getEmail())
                .fullName(existingEmployee.getFullName())
                .birthday(existingEmployee.getBirthday())
                .hobbies(existingEmployee.getHobbies())
                .build();

        when(employeeService.updateEmployee(eq(employeeId), any(UpdateEmployeeRequest.class))).thenReturn(updatedEmployee);

        // when
        mockMvc.perform(put("/employees/{employeeId}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEmployee.getId().toString()))
                .andExpect(jsonPath("$.email").value(updatedEmployee.getEmail()))
                .andExpect(jsonPath("$.fullName").value(updatedEmployee.getFullName()))
                .andExpect(jsonPath("$.birthday").value(updatedEmployee.getBirthday().toString()))
                .andExpect(jsonPath("$.hobbies").isArray())
                .andExpect(jsonPath("$.hobbies", hasSize(updatedEmployee.getHobbies().size())));
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        // given
        UUID employeeId = UUID.randomUUID();

        // when then
        mockMvc.perform(delete("/employees/{employeeId}", employeeId))
                .andExpect(status().isNoContent());

    }
}