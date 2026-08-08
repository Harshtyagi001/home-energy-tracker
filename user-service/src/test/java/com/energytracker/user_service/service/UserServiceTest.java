package com.energytracker.user_service.service;

import com.energytracker.user_service.dto.UserDto;
import com.energytracker.user_service.entity.User;
import com.energytracker.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .name("John Doe")
                .address("123 Main Street")
                .alerting(true)
                .energyAlertingThreshold(100.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUserDto = UserDto.builder()
                .id(1L)
                .email("john.doe@example.com")
                .name("John Doe")
                .address("123 Main Street")
                .alerting(true)
                .energyAlertingThreshold(100.0)
                .build();
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertEquals(testUserDto.getName(), result.getName());
        assertEquals(testUserDto.getAddress(), result.getAddress());
        assertEquals(testUserDto.isAlerting(), result.isAlerting());
        assertEquals(testUserDto.getEnergyAlertingThreshold(), result.getEnergyAlertingThreshold());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getName(), result.getName());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserDto result = userService.getUserById(999L);

        assertNull(result);
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto updateDto = UserDto.builder()
                .email("john.updated@example.com")
                .name("John Updated")
                .address("456 Oak Avenue")
                .alerting(false)
                .energyAlertingThreshold(150.0)
                .build();

        userService.updateUser(1L, updateDto);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserDto updateDto = UserDto.builder()
                .email("test@example.com")
                .name("Test User")
                .address("Test Address")
                .alerting(true)
                .energyAlertingThreshold(100.0)
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(999L, updateDto));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testCreateUserWithDifferentAlertingStatus() {
        UserDto userDtoNoAlert = UserDto.builder()
                .email("jane@example.com")
                .name("Jane Doe")
                .address("789 Pine Road")
                .alerting(false)
                .energyAlertingThreshold(50.0)
                .build();

        User savedUserNoAlert = User.builder()
                .id(2L)
                .email("jane@example.com")
                .name("Jane Doe")
                .address("789 Pine Road")
                .alerting(false)
                .energyAlertingThreshold(50.0)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUserNoAlert);

        UserDto result = userService.createUser(userDtoNoAlert);

        assertNotNull(result);
        assertFalse(result.isAlerting());
        assertEquals(50.0, result.getEnergyAlertingThreshold());

        verify(userRepository, times(1)).save(any(User.class));
    }
}
