package com.home_energy_tracker.device_service.repository;

import com.home_energy_tracker.device_service.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
