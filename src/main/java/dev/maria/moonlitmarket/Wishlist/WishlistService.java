package dev.maria.moonlitmarket.Wishlist;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Products.ProductsRepository;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public WishlistDTO addToWishlist(Long userId, Long productId){
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with the id: " + userId));

        Products product = productsRepository.findById(productId)
                          .orElseThrow(() -> new RuntimeException("Product not found with the id: " + productId));
        
        if(wishlistRepository.existsByUserIdAndProductId(userId, productId)){
            throw new RuntimeException("Product already in Wishlist");
        }

        Wishlist Wishlist = new Wishlist();
        Wishlist.setUser(user);
        Wishlist.setProduct(product);

        Wishlist savedWishlist = wishlistRepository.save(Wishlist);
        return toDTO(savedWishlist);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId){
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public List<WishlistDTO> getWishlist(Long userId){
        return wishlistRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private WishlistDTO toDTO(Wishlist wishlist) {
        return new WishlistDTO(
                wishlist.getId(),
                wishlist.getUser().getId(),
                wishlist.getProduct().getId()
        );
    }

}
