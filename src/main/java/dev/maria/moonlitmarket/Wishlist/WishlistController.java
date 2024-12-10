package dev.maria.moonlitmarket.Wishlist;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/user/wishlist/add")
    public ResponseEntity<WishlistDTO> addToWishlist(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            WishlistDTO wishlistDTO = wishlistService.addToWishlist(userId, productId);
            return ResponseEntity.ok(wishlistDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/user/wishlist/remove")
    public ResponseEntity<Void> removeFromWishlist(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            wishlistService.removeFromWishlist(userId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/user/wishlist/list/{id}")
    public ResponseEntity<List<WishlistDTO>> getWishlist(@RequestParam Long id) {
        try {
            List<WishlistDTO> wishlist = wishlistService.getWishlist(id);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
