package dev.maria.moonlitmarket.Users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Implement methods for user operations
}
