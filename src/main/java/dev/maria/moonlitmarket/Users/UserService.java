package dev.maria.moonlitmarket.Users;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService{

    private UserRepository repository;
    private PasswordEncoder encoder;

    public UserService (UserRepository repository, PasswordEncoder encoder){
        this.repository = repository;
        this.encoder = encoder;
    }

    public User createUser(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public List<User> getUsers(){
        return repository.findAll();
    }
    
    public Optional<User> getUserById(Long id){
        return repository.findById(id);
    }
    
    public User updateUser(Long id, User details) {
        return repository.findById(id).map(user -> {
            user.setUsername(details.getUsername());
            user.setPassword(encoder.encode(details.getPassword()));
            user.setEmail(details.getEmail());
            user.setRol(details.getRol());
            return repository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con userId: " + id));
    }

    public User updatePassword(Long id, String password){
        User user = repository.findById(id).orElse(null);
        if(user!=null){
            user.setPassword(password);
            return repository.save(user);
        }else{
            throw new RuntimeException("user not found with the id :"+id);
        }
    }
    

    public void deleteUser(Long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }else{
            throw new RuntimeException("Usuario no encontrado con userId: " + id);
        }
    }
}
