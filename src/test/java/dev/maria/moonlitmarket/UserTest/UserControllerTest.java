package dev.maria.moonlitmarket.UserTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.maria.moonlitmarket.Users.Role;
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
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);
        
        when(userService.createUser(userDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.createUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void listUsers_ShouldReturnListOfUsers_WhenAdminAuthorized() {
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        user1.setRole(Role.USER);
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("pass2");
        user2.setRole(Role.ADMIN);
        List<UserDTO> users = List.of(user1, user2);

        when(userService.getUsers()).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.listUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenSuccessful() {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("updateduser");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);
        when(userService.updateUser(userId, userDTO)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateUser(userId, userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("updateduser");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);
        when(userService.updateUser(userId, userDTO)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserDTO> response = userController.updateUser(userId, userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenSuccessful() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.of(new UserDTO()));

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updatePassword_ShouldReturnUpdatedUser_WhenSuccessful() {
        Long userId = 1L;
        String newPassword = "newPassword123";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("password");
        userDTO.setEmail("updated@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);

        when(userService.updatePassword(userId, newPassword)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updatePassword(userId, newPassword);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void updatePassword_ShouldReturnNotFound_WhenUserDoesNotExist() {
        Long userId = 1L;
        String newPassword = "newPassword123";
        when(userService.updatePassword(userId, newPassword)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserDTO> response = userController.updatePassword(userId, newPassword);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}

