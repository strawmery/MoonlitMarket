package dev.maria.moonlitmarket.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;
import jakarta.transaction.Transactional;

@Component
public class DataInicializater implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try{

            if(!userRepository.existsByUsername("root")){
                User root = new User();
                root.setUsername("root");
                root.setEmail("admin@gmail.com");
                root.setPassword(passwordEncoder.encode("passwordAdmin"));
                root.setRol("ADMIN");
                userRepository.save(root);
                System.out.println("usuario root creado en la base de datos");
            }else{
                System.out.println("usuario root cargado");
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    

}
