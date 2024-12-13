package dev.maria.moonlitmarket.UserTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.maria.moonlitmarket.Users.Role;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserDTO;
import dev.maria.moonlitmarket.Users.UserRepository;
import dev.maria.moonlitmarket.Users.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Test
    void createUser_ShouldReturnCreatedUserDTO() {
        UserDTO user = new UserDTO();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");
        user.setRole(Role.USER);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testUser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.USER);

        when(encoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void getUsers_ShouldReturnListOfUserDTOs() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setRole(Role.USER);
        user1.setAddress("address1");
        user1.setPhoneNumber("1234567890");
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setRole(Role.ADMIN);
        user2.setAddress("address2");
        user2.setPhoneNumber("9876543210");

        when(repository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDTO> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void getUserById_UserExists_ShouldReturnUserDTO() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setAddress("address");
        user.setPhoneNumber("1234567890");

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void getUserById_UserDoesNotExist_ShouldReturnEmpty() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateUser_UserExists_ShouldUpdateAndReturnUserDTO() {
        User existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("password");
        existingUser.setRole(Role.USER);
        existingUser.setAddress("address");
        existingUser.setPhoneNumber("1234567890");

        User updatedUser = new User();
        updatedUser.setUsername("newUser");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("encodedPassword");
        updatedUser.setRole(Role.ADMIN);
        updatedUser.setAddress("newAddress");
        updatedUser.setPhoneNumber("9876543210");

        UserDTO updateDetails = new UserDTO();
        updateDetails.setId(1L);
        updateDetails.setUsername("newUser");
        updateDetails.setEmail("new@example.com");
        updateDetails.setPassword("newPassword");
        updateDetails.setRole(Role.ADMIN);

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(encoder.encode("newPassword")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.updateUser(1L, updateDetails);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void updateUser_UserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        UserDTO updateDetails = new UserDTO();
        updateDetails.setId(1L);
        updateDetails.setUsername("newUser");
        updateDetails.setEmail("new@example.com");
        updateDetails.setPassword("newPassword");
        updateDetails.setRole(Role.ADMIN);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateDetails));
        assertEquals("User not found with the id: 1", exception.getMessage());
    }

    @Test
    void deleteUser_UserExists_ShouldDeleteUser() {
        when(repository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_UserDoesNotExist_ShouldThrowException() {
        when(repository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("User not found with userId: 1", exception.getMessage());
    }

    @Test
    void login_ValidCredentials_ShouldReturnSuccessMessage() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEmail("test@example.com");
        user.setAddress("testAddress");
        user.setPhoneNumber("1234567890");

        when(repository.findByUsername("testUser")).thenReturn(user);
        when(encoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        String result = userService.login("testUser", "plainPassword");

        assertEquals("Login successful", result);
    }

    @Test
    void login_InvalidCredentials_ShouldThrowException() {
        when(repository.findByUsername("testUser")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.login("testUser", "plainPassword"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updatePassword_ShouldUpdatePasswordForExistingUser() {
        Long userId = 1L;
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testUser");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("oldPassword");
        existingUser.setRole(Role.USER);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("testUser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setPassword(encodedPassword);
        updatedUser.setRole(Role.USER);

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(encoder.encode(newPassword)).thenReturn(encodedPassword);
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.updatePassword(userId, newPassword);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
        verify(repository).save(existingUser);
    }

}

