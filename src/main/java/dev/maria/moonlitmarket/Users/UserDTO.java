package dev.maria.moonlitmarket.Users;

import java.util.List;

import dev.maria.moonlitmarket.Wishlist.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String rol;
    private String address;
    private String phoneNumber;
    private List<Wishlist> Wishlists;
}