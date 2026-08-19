package com.home_energy_tracker.ingestion_service.service;

import com.home_energy_tracker.ingestion_service.dto.EnergyUsageDto;
import com.home_energy_tracker.kafka.event.EnergyUsageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IngestionService {
    private final KafkaPublisher kafkaPublisher;

    public IngestionService(KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    public void ingestEnergyUsageEvent(EnergyUsageDto input) {
        if (input.energyConsumed() == null) {
            throw new IllegalArgumentException("'energyConsumed' is required and must be a number");
        }
        EnergyUsageEvent event = EnergyUsageEvent.builder()
                .deviceId(input.deviceId())
                .energyConsumed(input.energyConsumed())
                .timestamp(input.timestamp())
                .build();
        log.info("Ingesting energy usage event: {}", event);

        // Publish using KafkaPublisher which handles async send and local retry queue on failures
        kafkaPublisher.publish(event);
    }
}
