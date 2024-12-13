package dev.maria.moonlitmarket.WishlistTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Products.ProductsRepository;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;
import dev.maria.moonlitmarket.Wishlist.Wishlist;
import dev.maria.moonlitmarket.Wishlist.WishlistDTO;
import dev.maria.moonlitmarket.Wishlist.WishlistRepository;
import dev.maria.moonlitmarket.Wishlist.WishlistService;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @InjectMocks
    private WishlistService wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void addToWishlist_ShouldAddProductToWishlist_WhenUserAndProductExist() {
        Long userId = 1L;
        Long productId = 100L;

        User user = new User();
        user.setId(userId);

        Products product = new Products();
        product.setId(productId);

        Wishlist wishlist = new Wishlist();
        wishlist.setId(10L);
        wishlist.setUser(user);
        wishlist.setProduct(product);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productsRepository.findById(productId)).thenReturn(Optional.of(product));
        when(wishlistRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistDTO result = wishlistService.addToWishlist(userId, productId);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(productId, result.getProductId());

        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void addToWishlist_ShouldThrowException_WhenProductAlreadyInWishlist() {
        Long userId = 1L;
        Long productId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRepository.findById(productId)).thenReturn(Optional.of(new Products()));
        when(wishlistRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wishlistService.addToWishlist(userId, productId)
        );

        assertEquals("Product already in Wishlist", exception.getMessage());
    }

    @Test
    void addToWishlist_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;
        Long productId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wishlistService.addToWishlist(userId, productId)
        );

        assertEquals("User not found with the id: 1", exception.getMessage());
    }

    @Test
    void addToWishlist_ShouldThrowException_WhenProductNotFound() {
        Long userId = 1L;
        Long productId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(productsRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wishlistService.addToWishlist(userId, productId)
        );

        assertEquals("Product not found with the id: 100", exception.getMessage());
    }

    @Test
    void removeFromWishlist_ShouldDeleteProductFromWishlist() {
        Long userId = 1L;
        Long productId = 100L;

        wishlistService.removeFromWishlist(userId, productId);

        verify(wishlistRepository).deleteByUserIdAndProductId(userId, productId);
    }

    @Test
    void getWishlist_ShouldReturnListOfWishlistDTOs() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Products product1 = new Products();
        product1.setId(100L);

        Products product2 = new Products();
        product2.setId(200L);

        Wishlist wishlist1 = new Wishlist();
        wishlist1.setId(10L);
        wishlist1.setUser(user);
        wishlist1.setProduct(product1);

        Wishlist wishlist2 = new Wishlist();
        wishlist2.setId(20L);
        wishlist2.setUser(user);
        wishlist2.setProduct(product2);

        List<Wishlist> wishlistEntities = Arrays.asList(wishlist1, wishlist2);

        when(wishlistRepository.findByUserId(userId)).thenReturn(wishlistEntities);

        List<WishlistDTO> result = wishlistService.getWishlist(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(100L, result.get(0).getProductId());
        assertEquals(20L, result.get(1).getId());
        assertEquals(200L, result.get(1).getProductId());
    }
}