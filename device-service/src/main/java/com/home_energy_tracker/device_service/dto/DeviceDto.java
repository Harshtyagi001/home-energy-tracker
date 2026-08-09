package com.home_energy_tracker.device_service.dto;

import com.home_energy_tracker.device_service.model.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DeviceDto {
    private String name;
    private Long id;
    private DeviceType type;
    private String location;
    private Long userId;
}
