package com.home_energy_tracker.ingestion_service.simulation;

import com.home_energy_tracker.ingestion_service.dto.EnergyUsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

@Slf4j
@Component
public class ContinousDataSimulation implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${simulation.requests-per-interval:1}")
    private int requestsPerInterval;

    @Value("${simulation.endpoint:http://localhost:8082/api/v1/ingestion}")
    private String ingestionEndpoint;


    @Override
    public void run(String... args) throws Exception {
        log.info("ContinousDataSimulation started...");
        // Send an initial batch on startup so users see activity immediately in dev
        try {
            sendMockData();
        } catch (Exception e) {
            log.error("Initial simulation run failed: {}", e.getMessage());
        }
    }


    @Scheduled(fixedRateString = "${simulation.interval-ms:5000}")
    public void sendMockData() {
        for (int i = 0; i < requestsPerInterval; i++) {
            EnergyUsageDto dto = new EnergyUsageDto(
                    random.nextLong(1, 6),
                    Math.round(random.nextDouble(0.0, 2.0) * 100.0) / 100.0,
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            );

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<EnergyUsageDto> request = new HttpEntity<>(dto, headers);
                restTemplate.postForEntity(ingestionEndpoint, request, Void.class);

                log.info ("Sent mock data: {}", dto);
            } catch (Exception e) {
                log.error("Failed to send data: {}", e.getMessage());
            }
        }
    }
}