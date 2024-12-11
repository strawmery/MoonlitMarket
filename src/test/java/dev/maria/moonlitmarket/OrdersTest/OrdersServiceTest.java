package dev.maria.moonlitmarket.OrdersTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.maria.moonlitmarket.Orders.Orders;
import dev.maria.moonlitmarket.Orders.OrdersRepository;
import dev.maria.moonlitmarket.Orders.OrdersService;
import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Users.User;
import dev.maria.moonlitmarket.Users.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrdersService ordersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // Preparar datos
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("Test User");

        Products product1 = new Products();
        product1.setId(1L);
        product1.setPrice(100.0);

        Products product2 = new Products();
        product2.setId(2L);
        product2.setPrice(150.0);

        Orders expectedOrder = new Orders();
        expectedOrder.setUser(user);
        expectedOrder.setProducts(Arrays.asList(product1, product2));
        expectedOrder.setOrderDate(LocalDateTime.now());
        expectedOrder.setStatus("pending");

        // Configurar mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ordersRepository.save(any(Orders.class))).thenReturn(expectedOrder);

        // Ejecutar método
        Orders actualOrder = ordersService.createOrder(userId, Arrays.asList(product1, product2));

        // Verificar resultados
        assertNotNull(actualOrder);
        assertEquals(user, actualOrder.getUser());
        assertEquals(2, actualOrder.getProducts().size());
        assertEquals("pending", actualOrder.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(ordersRepository, times(1)).save(any(Orders.class));
    }

    @Test
    void testCreateOrder_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordersService.createOrder(userId, Arrays.asList(new Products()));
        });

        assertEquals("User not found with the id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(ordersRepository);
    }

    @Test
    void testUpdateOrderStatus() {
        Long orderId = 1L;
        String newStatus = "completed";

        Orders existingOrder = new Orders();
        existingOrder.setId(orderId);
        existingOrder.setStatus("pending");

        Orders updatedOrder = new Orders();
        updatedOrder.setId(orderId);
        updatedOrder.setStatus(newStatus);

        when(ordersRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(ordersRepository.save(any(Orders.class))).thenReturn(updatedOrder);

        Orders actualOrder = ordersService.updateOrderStatus(orderId, newStatus);

        assertNotNull(actualOrder);
        assertEquals(newStatus, actualOrder.getStatus());
        verify(ordersRepository, times(1)).findById(orderId);
        verify(ordersRepository, times(1)).save(existingOrder);
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        Long orderId = 1L;

        when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ordersService.updateOrderStatus(orderId, "completed");
        });

        assertEquals("Order not found with the id: " + orderId, exception.getMessage());
        verify(ordersRepository, times(1)).findById(orderId);
        verify(ordersRepository, never()).save(any(Orders.class));
    }
}

