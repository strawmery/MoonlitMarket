package dev.maria.moonlitmarket.WishlistTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.maria.moonlitmarket.Wishlist.WishlistController;
import dev.maria.moonlitmarket.Wishlist.WishlistDTO;
import dev.maria.moonlitmarket.Wishlist.WishlistService;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @InjectMocks
    private WishlistController wishlistController;

    @Mock
    private WishlistService wishlistService;

    @Test
    void addToWishlist_ShouldReturnWishlistDTO_WhenSuccessfullyAdded() {
        Long userId = 1L;
        Long productId = 100L;

        WishlistDTO wishlistDTO = new WishlistDTO(1L, userId, productId);

        when(wishlistService.addToWishlist(userId, productId)).thenReturn(wishlistDTO);

        ResponseEntity<WishlistDTO> response = wishlistController.addToWishlist(userId, productId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(userId, response.getBody().getUserId());
        assertEquals(productId, response.getBody().getProductId());
    }

    @Test
    void addToWishlist_ShouldThrowException_WhenServiceFails() {
        Long userId = 1L;
        Long productId = 100L;

        when(wishlistService.addToWishlist(userId, productId)).thenThrow(new RuntimeException("Service exception"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wishlistController.addToWishlist(userId, productId)
        );

        assertEquals("java.lang.RuntimeException: Service exception", exception.getMessage());
    }

    @Test
    void removeFromWishlist_ShouldReturnOk_WhenSuccessfullyRemoved() {
        Long userId = 1L;
        Long productId = 100L;

        ResponseEntity<Void> response = wishlistController.removeFromWishlist(userId, productId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(wishlistService).removeFromWishlist(userId, productId);
    }

    @Test
    void removeFromWishlist_ShouldThrowException_WhenServiceFails() {
        Long userId = 1L;
        Long productId = 100L;

        doThrow(new RuntimeException("Service exception"))
                .when(wishlistService).removeFromWishlist(userId, productId);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wishlistController.removeFromWishlist(userId, productId)
        );

        assertEquals("java.lang.RuntimeException: Service exception", exception.getMessage());
    }

    @Test
    void getWishlist_ShouldReturnWishlistList_WhenUserHasItems() {
        Long userId = 1L;

        WishlistDTO wishlist1 = new WishlistDTO(1L, userId, 100L);
        WishlistDTO wishlist2 = new WishlistDTO(2L, userId, 200L);
        List<WishlistDTO> wishlist = Arrays.asList(wishlist1, wishlist2);

        when(wishlistService.getWishlist(userId)).thenReturn(wishlist);

        ResponseEntity<List<WishlistDTO>> response = wishlistController.getWishlist(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(100L, response.getBody().get(0).getProductId());
        assertEquals(200L, response.getBody().get(1).getProductId());
    }

    @Test
    void getWishlist_ShouldThrowException_WhenServiceFails() {
        Long userId = 1L;

        when(wishlistService.getWishlist(userId)).thenThrow(new RuntimeException("Service exception"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> wishlistController.getWishlist(userId)
        );

        assertEquals("java.lang.RuntimeException: Service exception", exception.getMessage());
    }
}
