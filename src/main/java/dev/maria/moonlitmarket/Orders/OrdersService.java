package dev.maria.moonlitmarket.Orders;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;

@Service
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
        order.setStatus(Status.PENDING);

        products.forEach(product -> product.setOrder(order));

        return repository.save(order);
    }

    public Orders updateOrderStatus(Long orderId, Status newStatus) {
        Orders order = repository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found with the id: " + orderId));
        
        order.setStatus(newStatus);

        return repository.save(order);
    }
}
