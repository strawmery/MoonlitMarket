package dev.maria.moonlitmarket.OrdersTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import dev.maria.moonlitmarket.Orders.Orders;
import dev.maria.moonlitmarket.Orders.OrdersRepository;
import dev.maria.moonlitmarket.Orders.OrdersService;
import dev.maria.moonlitmarket.Orders.Status;
import dev.maria.moonlitmarket.Users.UserRepository;

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
    void testUpdateOrderStatus() {
        Long orderId = 1L;
        Status newStatus = Status.DELIVERED;

        Orders existingOrder = new Orders();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Status.PENDING);

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
            ordersService.updateOrderStatus(orderId, Status.DELIVERED);
        });

        assertEquals("Order not found with the id: " + orderId, exception.getMessage());
        verify(ordersRepository, times(1)).findById(orderId);
        verify(ordersRepository, never()).save(any(Orders.class));
    }
}

