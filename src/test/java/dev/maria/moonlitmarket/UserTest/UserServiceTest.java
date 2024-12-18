package dev.maria.moonlitmarket.UserTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    void updateUser_AuthenticatedUserUpdatesOwnProfile_ShouldUpdateUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("authenticatedUser");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldPassword");
        existingUser.setRole(Role.USER);
        existingUser.setAddress("oldAddress");
        existingUser.setPhoneNumber("1234567890");

        UserDTO updateDetails = new UserDTO();
        updateDetails.setUsername("authenticatedUser");
        updateDetails.setEmail("new@example.com");
        updateDetails.setPassword("newPassword");
        updateDetails.setAddress("newAddress");
        updateDetails.setPhoneNumber("9876543210");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        UserDTO result = userService.updateUser(1L, updateDetails);

        assertNotNull(result);
        assertEquals("authenticatedUser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newAddress", result.getAddress());
        assertEquals("9876543210", result.getPhoneNumber());
        assertEquals("encodedNewPassword", existingUser.getPassword());

        SecurityContextHolder.clearContext();
    }
    
    @Test
    void updateUser_UserDoesNotExist_ShouldThrowException() {
        UserDTO updateDetails = new UserDTO();
        updateDetails.setId(1L);
        updateDetails.setUsername("newUser");
        updateDetails.setEmail("new@example.com");
        updateDetails.setPassword("newPassword");
        updateDetails.setRole(Role.ADMIN);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateDetails));
        assertEquals("Cannot invoke \"org.springframework.security.core.Authentication.getName()\" because the return value of \"org.springframework.security.core.context.SecurityContext.getAuthentication()\" is null", exception.getMessage());
    }

    @Test
    void deleteUser_AuthenticatedUserDeletesOwnAccount_ShouldDeleteUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("authenticatedUser");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(repository).deleteById(1L);

        SecurityContextHolder.clearContext();
    }


    @Test
    void deleteUser_UserDoesNotExist_ShouldThrowException() {
        // Simula el contexto de seguridad
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");
    
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    
        SecurityContextHolder.setContext(securityContext);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("User not found with the id: 1", exception.getMessage());

        SecurityContextHolder.clearContext();
    }
    @Test
    void deleteUser_AuthenticatedUserTriesToDeleteAnotherUser_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("anotherUser");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertEquals("You are not authorized to delete this user", exception.getMessage());

        SecurityContextHolder.clearContext();
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
    void updatePassword_AuthenticatedUserUpdatesOwnPassword_ShouldUpdatePassword() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");
    
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    
        SecurityContextHolder.setContext(securityContext);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("authenticatedUser");
        existingUser.setPassword("oldPassword");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updatePassword(1L, "newPassword");

        assertNotNull(result);
        assertEquals("authenticatedUser", result.getUsername());
        assertEquals("encodedNewPassword", existingUser.getPassword());
        verify(repository).save(existingUser);

        SecurityContextHolder.clearContext();
    }

    @Test
    void updatePassword_UserDoesNotExist_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updatePassword(1L, "newPassword"));
        assertEquals("User not found with the id: 1", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void updatePassword_AuthenticatedUserTriesToUpdateAnotherUserPassword_ShouldThrowException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("authenticatedUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("anotherUser");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updatePassword(1L, "newPassword"));
        assertEquals("You are not authorized to update password", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

}

