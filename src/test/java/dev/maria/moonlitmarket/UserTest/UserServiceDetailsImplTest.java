package dev.maria.moonlitmarket.UserTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import dev.maria.moonlitmarket.Users.Role;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;
import dev.maria.moonlitmarket.Users.UserServiceDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceDetailsImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceDetailsImpl userDetailsService;


    @Test
    void loadUserByUsername() {

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setAddress("testAddress");
        user.setPhoneNumber("1234567890");
        when(repository.findByUsername("testuser")).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        verify(repository).findByUsername("testuser");
    }

        @Test
    void loadUserByUsernameThrowsException() {

        when(repository.findByUsername("unknownuser")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("unknownuser")
        );

        assertEquals("User not found", exception.getMessage());
        verify(repository).findByUsername("unknownuser");
    }

}
