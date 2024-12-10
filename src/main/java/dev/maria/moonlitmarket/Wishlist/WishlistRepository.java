package dev.maria.moonlitmarket.Wishlist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    public void deleteByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findByUserId(Long userId);

}
