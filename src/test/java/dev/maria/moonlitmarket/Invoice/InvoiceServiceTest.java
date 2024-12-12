package dev.maria.moonlitmarket.Invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dev.maria.moonlitmarket.Orders.Orders;
import dev.maria.moonlitmarket.Orders.OrdersRepository;
import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Users.User;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private Orders order;

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");

        Products product1 = new Products();
        product1.setId(101L);
        product1.setName("Product A");
        product1.setPrice(50.0);

        Products product2 = new Products();
        product2.setId(102L);
        product2.setName("Product B");
        product2.setPrice(30.0);

        order = new Orders();
        order.setId(1001L);
        order.setUser(user);
        order.setProducts(Arrays.asList(product1, product2));
        order.setOrderDate(LocalDateTime.now());

        invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber("INV-1001-12345");
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setTotalAmount(80.0);
        invoice.setTaxAmount(16.8);
        invoice.setStatus("pending");
    }

    @Test
    void testCreateInvoice_Success() {
        Long orderId = 1001L;
        Mockito.when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(invoice);

        Invoice createdInvoice = invoiceService.createInvoice(orderId);

        assertNotNull(createdInvoice);
        assertEquals("INV-1001-12345", createdInvoice.getInvoiceNumber());
        assertEquals(80.0, createdInvoice.getTotalAmount());
        assertEquals(16.8, createdInvoice.getTaxAmount());
        assertEquals("pending", createdInvoice.getStatus());
    }

    @Test
    void testCreateInvoice_OrderNotFound() {
        Long orderId = 999L;
        Mockito.when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> invoiceService.createInvoice(orderId));
        assertEquals("Order not found with the id: 999", exception.getMessage());
    }

    @Test
    void testDownloadInvoice_Success() {
        Long orderId = 1001L;
    
        Mockito.when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
    
        byte[] pdfBytes = invoiceService.dowloadInvoice(orderId);
    
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
    
    

    @Test
    void testGetTotalAmount() {
        double totalAmount = invoiceService.getTotalAmount();

        assertEquals(0.0, totalAmount);
    }
}
