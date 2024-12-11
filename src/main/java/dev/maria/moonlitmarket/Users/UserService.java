package dev.maria.moonlitmarket.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        user.setRol(userDTO.getRol());

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
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with the id: " + id));

        user.setUsername(details.getUsername());
        if (details.getPassword() != null && !details.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(details.getPassword()));
        }
        user.setEmail(details.getEmail());
        user.setRol(details.getRol());

        User updatedUser = repository.save(user);
        return toDTO(updatedUser);
    }

    public UserDTO updatePassword(Long id, String password) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with the id: " + id));

        user.setPassword(encoder.encode(password));
        User updatedUser = repository.save(user);
        return toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found with userId: " + id);
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
        dto.setRol(user.getRol());
        return dto;
    }
}
