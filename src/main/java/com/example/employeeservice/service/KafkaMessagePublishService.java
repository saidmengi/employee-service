package com.example.employeeservice.service;

import com.example.employeeservice.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagePublishService {

    @Value("${spring.kafka.topic}")
    private String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Employee event) {
        try {
            kafkaTemplate.send(topic, event.getId().toString(), objectMapper.writeValueAsString(event));
            log.info("employee event published");
        } catch (JsonProcessingException e) {
            log.error("could not sent employee event");
        }
    }

    public void publishDeletion(UUID employeeId) {
        kafkaTemplate.send(topic, employeeId.toString(), null);
        log.info("employee delete event published");
    }
}
