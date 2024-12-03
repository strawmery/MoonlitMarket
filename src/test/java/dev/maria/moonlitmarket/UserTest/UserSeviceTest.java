package dev.maria.moonlitmarket.UserTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;
import dev.maria.moonlitmarket.Users.UserService;

public class UserSeviceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void registerUser(){
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@gmail.com");
        user.setPassword("testpassword");
        user.setRol("USER");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("test");
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("encodedpassword");
        savedUser.setRol("USER");

        when(encoder.encode("testpassword")).thenReturn("encodedpassword");
        when(repository.save(user)).thenReturn(savedUser);;

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("test", createdUser.getUsername());
        assertEquals("test@gmail.com", createdUser.getEmail());
        verify(encoder).encode("testpassword");
        verify(repository).save(user);
    }

    //TODO: review and fix the code
    @Test
    void listUsers(){
        User user1 = new User();
        User user2 = new User();

        when(repository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsers();
        assertEquals(2, users.size());
        verify(repository).findAll();
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(1L, foundUser.get().getId());
        verify(repository).findById(1L);
    }

    @Test
    void getUserByIdthrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isEmpty());
        verify(repository).findById(1L);
    }

    @Test
    void updateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");
        existingUser.setRol("USER");

        User updatedDetails = new User();
        updatedDetails.setUsername("newUsername");
        updatedDetails.setPassword("newPassword");
        updatedDetails.setRol("ADMIN");

        when(repository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(repository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(1L, updatedDetails);

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        assertEquals("ADMIN", updatedUser.getRol());
        verify(encoder).encode("newPassword");
        verify(repository).save(existingUser);
    }

    @Test
    void updatePassword() {
        // Datos de prueba
        Long userId = 1L;
        String newPassword = "newEncodedPassword";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword("oldPassword");

        // Configurar mocks
        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(existingUser)).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método
        User updatedUser = userService.updatePassword(userId, newPassword);

        // Verificar resultados
        assertNotNull(updatedUser);
        assertEquals(newPassword, updatedUser.getPassword());
        verify(repository).findById(userId);
        verify(repository).save(existingUser);
    }

    @Test
    void updatePasswordNotFound() {

        Long userId = 1L;
        String newPassword = "newEncodedPassword";

        when(repository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(userId, newPassword);
        });

        assertEquals("user not found with the id :" + userId, exception.getMessage());
        verify(repository).findById(userId);
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void updateUserthrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, new User());
        });

        assertEquals("user not found with the id :1", exception.getMessage());
    }

    @Test
    void deleteUser() {
        when(repository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteUserthrowsException() {
        when(repository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("Usuario no encontrado con userId: 1", exception.getMessage());
    }

}
