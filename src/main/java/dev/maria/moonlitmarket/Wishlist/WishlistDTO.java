package dev.maria.moonlitmarket.Wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistDTO {
    private Long id;
    private Long userId;
    private Long productId;
}
