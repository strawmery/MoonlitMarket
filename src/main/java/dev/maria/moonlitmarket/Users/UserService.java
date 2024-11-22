package dev.maria.moonlitmarket.Users;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService{

    private final UserRepository repository;
    private final PasswordEncoder encoder;

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
    
    public User updateUser(Long id, User details){
        return repository.findById(id).map(user -> {
            user.setUsername(user.getUsername());
            user.setPassword(encoder.encode(user.getPassword()));
            user.setEmail(user.getEmail());
            user.setRol(user.getRol());
            return repository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con userId: " + id));
    }

    public void deleteUser(Long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }else{
            throw new RuntimeException("Usuario no encontrado con userId: " + id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_"+user.getRol()))
        );
    }


}
