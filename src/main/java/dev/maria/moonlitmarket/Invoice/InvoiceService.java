package dev.maria.moonlitmarket.Invoice;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.maria.moonlitmarket.Orders.Orders;
import dev.maria.moonlitmarket.Orders.OrdersRepository;
import jakarta.transaction.Transactional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Transactional
    public Invoice createInvoice(Long orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found with the id: " + orderId));

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceNumber("INV-"+ orderId + "-" + System.currentTimeMillis());
        invoice.setIssueDate(LocalDateTime.now());

        double totalAmount = order.getProducts().stream().mapToDouble(product -> product.getPrice()).sum();
        double taxAmount = totalAmount * 0.21; //21% IVA

        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setStatus("pending");

        return invoiceRepository.save(invoice);
    }

}
