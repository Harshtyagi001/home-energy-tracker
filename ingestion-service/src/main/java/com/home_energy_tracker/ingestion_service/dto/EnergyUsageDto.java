package com.home_energy_tracker.ingestion_service.dto;

import java.time.Instant;

public record EnergyUsageDto(
        Long deviceId,
        Double energyConsumed,
        Instant timestamp
) {}