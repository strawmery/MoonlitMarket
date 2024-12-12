package dev.maria.moonlitmarket.OrdersTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dev.maria.moonlitmarket.Orders.Orders;
import dev.maria.moonlitmarket.Orders.OrdersController;
import dev.maria.moonlitmarket.Orders.OrdersService;
import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Users.Role;
import dev.maria.moonlitmarket.Users.User;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @InjectMocks
    private OrdersController ordersController;

    @Mock
    private OrdersService ordersService;

    private Orders order;
    private Products product1;
    private Products product2;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setUsername("John Doe");
        user.setEmail("T7e6o@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setAddress("123 Main St");
        user.setPhoneNumber("123-456-7890");

        product1 = new Products();
        product1.setId(101L);
        product1.setName("Product A");

        product2 = new Products();
        product2.setId(102L);
        product2.setName("Product B");


        order = new Orders();
        order.setId(1001L);
        order.setUser(user);
        order.setProducts(Arrays.asList(product1, product2));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("pending");
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        Long userId = 1L;
        List<Products> products = Arrays.asList(product1, product2);
        Mockito.when(ordersService.createOrder(userId, products)).thenReturn(order);

        // Act
        ResponseEntity<Orders> response = ordersController.createOrder(userId, products);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().getId());
        assertEquals("pending", response.getBody().getStatus());
    }

    @Test
    void testCreateOrder_BadRequest() {
        // Arrange
        Long userId = 99L;
        List<Products> products = Arrays.asList(product1, product2);
        Mockito.when(ordersService.createOrder(userId, products)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<Orders> response = ordersController.createOrder(userId, products);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        Long orderId = 1001L;
        String status = "shipped";
        order.setStatus(status);
        Mockito.when(ordersService.updateOrderStatus(orderId, status)).thenReturn(order);

        // Act
        ResponseEntity<Orders> response = ordersController.updateOrderStatus(orderId, status);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().getId());
        assertEquals("shipped", response.getBody().getStatus());
    }

    @Test
    void testUpdateOrderStatus_BadRequest() {
        // Arrange
        Long orderId = 999L;
        String status = "shipped";
        Mockito.when(ordersService.updateOrderStatus(orderId, status)).thenThrow(new RuntimeException("Order not found"));

        ResponseEntity<Orders> response = ordersController.updateOrderStatus(orderId, status);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
