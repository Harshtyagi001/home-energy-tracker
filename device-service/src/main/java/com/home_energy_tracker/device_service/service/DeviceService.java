package com.home_energy_tracker.device_service.service;

import com.home_energy_tracker.device_service.dto.DeviceDto;
import com.home_energy_tracker.device_service.entity.Device;
import com.home_energy_tracker.device_service.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private DeviceRepository deviceRepository;

    DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDto mapToDto(Device device) {
        return DeviceDto.builder()
                .id(device.getId())
                .name(device.getName())
                .type(device.getType())
                .location(device.getLocation())
                .userId(device.getUserId())
                .build();
    }

    public DeviceDto getDeviceById(Long id) {
        Device deviceDetails = deviceRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Device with id " + id + " not found"));
        return mapToDto(deviceDetails);
    }

    public DeviceDto createDevice(DeviceDto createRequest) {
        Device device = Device.builder()
                .name(createRequest.getName())
                .type(createRequest.getType())
                .location(createRequest.getLocation())
                .userId(createRequest.getUserId())
                .build();
        Device savedDevice = deviceRepository.save(device);
        return mapToDto(savedDevice);
    }

    public void updateDevice(Long id, DeviceDto deviceDto) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + id));
        device.setName(deviceDto.getName());
        device.setType(deviceDto.getType());
        device.setLocation(deviceDto.getLocation());
        device.setUserId(deviceDto.getUserId());
        deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + id));
        deviceRepository.delete(device);
    }
}
