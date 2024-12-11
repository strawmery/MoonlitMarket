package dev.maria.moonlitmarket.UserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import dev.maria.moonlitmarket.Users.User;

public class UserTest {

    @Test
    void testConstructorUser() {
        Long id = 1L;
        String username = "testUser";
        String email = "testUser@gmail.com";
        String password = "testPassword";
        String rol = "admin";
        String address = "testAddress";
        String phoneNumber = "testPhoneNumber";
        User user = new User();

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(rol, user.getRol());

    }

    @Test
    void testId(){
        User user = new User();
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void testUsername(){
        User user = new User();
        user.setUsername("testUser");
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testEmail(){
        User user = new User();
        user.setEmail("testUser@gmail.com");
        assertEquals("testUser@gmail.com", user.getEmail());
    }
    
    @Test
    void testPassword(){
        User user = new User();
        user.setPassword("testPassword");
        assertEquals("testPassword", user.getPassword());
    }

    @Test
    void testRol(){
        User user = new User();
        user.setRol("admin");
        assertEquals("admin", user.getRol());
    }
}
