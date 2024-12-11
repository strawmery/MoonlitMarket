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

import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserDTO;
import dev.maria.moonlitmarket.Users.UserRepository;
import dev.maria.moonlitmarket.Users.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Test
    void createUser_ShouldReturnCreatedUserDTO() {
        // Arrange
        UserDTO user = new UserDTO();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");
        user.setRol("USER");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testUser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRol("USER");

        when(encoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDTO result = userService.createUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRol());
    }

    @Test
    void getUsers_ShouldReturnListOfUserDTOs() {
        // Arrange
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setRol("USER");
        user1.setAddress("address1");
        user1.setPhoneNumber("1234567890");
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setRol("ADMIN");
        user2.setAddress("address2");
        user2.setPhoneNumber("9876543210");

        when(repository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserDTO> result = userService.getUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void getUserById_UserExists_ShouldReturnUserDTO() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRol("USER");
        user.setAddress("address");
        user.setPhoneNumber("1234567890");

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<UserDTO> result = userService.getUserById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void getUserById_UserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> result = userService.getUserById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void updateUser_UserExists_ShouldUpdateAndReturnUserDTO() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("password");
        existingUser.setRol("USER");
        existingUser.setAddress("address");
        existingUser.setPhoneNumber("1234567890");

        User updatedUser = new User();
        updatedUser.setUsername("newUser");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("encodedPassword");
        updatedUser.setRol("ADMIN");
        updatedUser.setAddress("newAddress");
        updatedUser.setPhoneNumber("9876543210");

        UserDTO updateDetails = new UserDTO();
        updateDetails.setId(1L);
        updateDetails.setUsername("newUser");
        updateDetails.setEmail("new@example.com");
        updateDetails.setPassword("newPassword");
        updateDetails.setRol("ADMIN");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(encoder.encode("newPassword")).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDTO result = userService.updateUser(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRol());
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
        updateDetails.setRol("ADMIN");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateDetails));
        assertEquals("User not found with the id: 1", exception.getMessage());
    }

    @Test
    void deleteUser_UserExists_ShouldDeleteUser() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_UserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(repository.existsById(1L)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("User not found with userId: 1", exception.getMessage());
    }

    @Test
    void login_ValidCredentials_ShouldReturnSuccessMessage() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setRol("USER");
        user.setEmail("test@example.com");
        user.setAddress("testAddress");
        user.setPhoneNumber("1234567890");

        when(repository.findByUsername("testUser")).thenReturn(user);
        when(encoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        // Act
        String result = userService.login("testUser", "plainPassword");

        // Assert
        assertEquals("Login successful", result);
    }

    @Test
    void login_InvalidCredentials_ShouldThrowException() {
        // Arrange
        when(repository.findByUsername("testUser")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.login("testUser", "plainPassword"));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updatePassword_ShouldUpdatePasswordForExistingUser() {
        // Arrange
        Long userId = 1L;
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("testUser");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("oldPassword");
        existingUser.setRol("USER");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("testUser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setPassword(encodedPassword);
        updatedUser.setRol("USER");

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(encoder.encode(newPassword)).thenReturn(encodedPassword);
        when(repository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDTO result = userService.updatePassword(userId, newPassword);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRol());
        verify(repository).save(existingUser); // Verifica que se guardó el usuario actualizado
    }

}

