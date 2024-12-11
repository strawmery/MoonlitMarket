package dev.maria.moonlitmarket.Orders;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;

public class OrdersService {

    @Autowired
    private OrdersRepository repository;

    @Autowired
    private UserRepository userRepository;

    public Orders createOrder(Long  userId, List<Products> products) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found with the id: " + userId));

        Orders order = new Orders();
        order.setUser(user);
        order.setProducts(products);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("pending");

        products.forEach(product -> product.setOrder(order));

        return repository.save(order);
    }

    public Orders updateOrderStatus(Long orderId, String status){
        Orders order = repository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found with the id: " + orderId));
        
        order.setStatus(status);

        return repository.save(order);
    }
}
