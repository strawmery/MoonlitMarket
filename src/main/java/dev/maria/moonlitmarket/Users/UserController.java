package dev.maria.moonlitmarket.Users;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping(path = "/api")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService =userService;
    }

    //publico
    @PostMapping(path = "/public/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //admin
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path = "/admin/listusers")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    //users
    @PutMapping("/user/update/{id}")
    public ResponseEntity<User> updateUser (@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //users
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //users
    @PatchMapping("/user/password/{id}")
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestParam String password){
        try {
           User updatedPassword = userService.updatePassword(id, password);
           return ResponseEntity.ok(updatedPassword); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
}
