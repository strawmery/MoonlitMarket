package dev.maria.moonlitmarket.UserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.maria.moonlitmarket.Users.UserController;
import dev.maria.moonlitmarket.Users.UserDTO;
import dev.maria.moonlitmarket.Users.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void createUser_ShouldReturnCreatedUser_WhenSuccessful() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setRol("USER");
        
        when(userService.createUser(userDTO)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.createUser(userDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void listUsers_ShouldReturnListOfUsers_WhenAdminAuthorized() {
        // Arrange
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        user1.setRol("USER");
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("pass2");
        user2.setRol("ADMIN");
        List<UserDTO> users = List.of(user1, user2);

        when(userService.getUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = userController.listUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenSuccessful() {
        // Arrange
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("updateduser");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("password123");
        userDTO.setRol("USER");
        when(userService.updateUser(userId, userDTO)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(userId, userDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("updateduser");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("password123");
        userDTO.setRol("USER");
        when(userService.updateUser(userId, userDTO)).thenThrow(new RuntimeException("User not found"));

        // Act
        ResponseEntity<UserDTO> response = userController.updateUser(userId, userDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenSuccessful() {
        // Arrange
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.of(new UserDTO()));

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updatePassword_ShouldReturnUpdatedUser_WhenSuccessful() {
        // Arrange
        Long userId = 1L;
        String newPassword = "newPassword123";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("password");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("password123");
        userDTO.setRol("USER");

        when(userService.updatePassword(userId, newPassword)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = userController.updatePassword(userId, newPassword);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updatePassword_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        String newPassword = "newPassword123";
        when(userService.updatePassword(userId, newPassword)).thenThrow(new RuntimeException("User not found"));

        // Act
        ResponseEntity<UserDTO> response = userController.updatePassword(userId, newPassword);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}

