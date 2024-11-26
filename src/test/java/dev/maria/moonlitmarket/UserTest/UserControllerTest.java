package dev.maria.moonlitmarket.UserTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserController;
import dev.maria.moonlitmarket.Users.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test", "test@example.com", "testpassword", "USER");
    }

    @Test
    void createUser() {

        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void listUsers () {
       List<User> users = Arrays.asList(user, new User(2L, "test2", "test2@gmail.com", "password2", "USER"));
       when(userService.getUsers()).thenReturn(users);

       ResponseEntity<List<User>> response = userController.listUsers();

       assertEquals(HttpStatus.OK, response.getStatusCode());
       assertEquals(2, response.getBody().size());
    }

    @Test
    void updateUser() {
        User updatedUser = new User(1L, "John Smith", "johnsmith@example.com", "password", "USER");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    void updateUserNotFound() {

        when(userService.updateUser(eq(999L), any(User.class))).thenThrow(new RuntimeException("User not found"));


        ResponseEntity<User> response = userController.updateUser(999L, user);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void deleteUserNoContent() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUserNotFound() {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = userController.deleteUser(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

 @Test
    void updatePassword() {

        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        Long userId = 1L;
        String newPassword = "newPassword";
        User mockUser = new User(userId, "testUser", "testUser@gmail.com", "oldPassword", "USER");
        mockUser.setPassword(newPassword);

        when(userService.updatePassword(userId, newPassword)).thenReturn(mockUser);

        ResponseEntity<User> response = userController.updatePassword(userId, newPassword);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newPassword, response.getBody().getPassword());
        verify(userService).updatePassword(userId, newPassword);
    }

    @Test
    void updatePasswordUserNotFound() {

        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);

        Long userId = 1L;
        String newPassword = "newPassword";

        when(userService.updatePassword(userId, newPassword))
                .thenThrow(new RuntimeException("User not found with the id: " + userId));

        ResponseEntity<User> response = userController.updatePassword(userId, newPassword);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).updatePassword(userId, newPassword);
    }


}
