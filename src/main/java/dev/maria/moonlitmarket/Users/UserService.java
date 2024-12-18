package dev.maria.moonlitmarket.Users;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setRole(Role.USER);
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        User savedUser = repository.save(user);
        return toDTO(savedUser);
    }

    public List<UserDTO> getUsers() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    public UserDTO updateUser(Long id, UserDTO details) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = repository.findById(id).orElseThrow(()-> new RuntimeException("user not found with the id: " + id));

        if(!details.getUsername().equals(authenticatedUsername)){
            throw new RuntimeException("You are not authorized to update this user");
        }

        user.setUsername(details.getUsername());
        user.setEmail(details.getEmail());
        user.setAddress(details.getAddress());
        user.setPhoneNumber(details.getPhoneNumber());

        if (details.getPassword() != null && !details.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(details.getPassword()));
        }

        User updatedUser = repository.save(user);
        return toDTO(updatedUser);
    }

    public UserDTO updatePassword(Long id, String password) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found with the id: " + id));

        if(!user.getUsername().equals(authenticatedUsername)){
            throw new RuntimeException("You are not authorized to update password");
        }

        user.setPassword(encoder.encode(password));
        User updatedUser = repository.save(user);
        return toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found with the id: " + id));

        if(!user.getUsername().equals(authenticatedUsername)){
            throw new RuntimeException("You are not authorized to delete this user");
        }

        repository.deleteById(id);
    }

    public String login(String username, String password) {
        User user = repository.findByUsername(username);

        if(user == null){
            throw new RuntimeException("User not found");
        }

        if(!encoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        return "Login successful";
    }

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
}
