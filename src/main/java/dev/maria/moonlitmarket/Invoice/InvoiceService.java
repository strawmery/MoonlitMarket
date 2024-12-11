package dev.maria.moonlitmarket.Invoice;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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

    @Autowired
    private Orders order;

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

    public byte[] dowloadInvoice(Long orderId) {
        try (PDDocument document = new PDDocument(); 
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
                
                PDPage page = new PDPage();
                document.addPage(page);

                try(PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 700);

                    contentStream.showText("Invoice for Order ID: " + orderId);
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Customer: "+ order.getUser().getUsername());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Product A -"+ order.getProducts().get(0).getName());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Product B -"+ order.getProducts().get(1).getName());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Total: "+ getTotalAmount());

                    contentStream.endText();
                }
                    document.save(outputStream);
                    return outputStream.toByteArray();
                
             }catch(Exception e){
                throw new RuntimeException("Error downloading invoice",e);
             }
    }

    public double getTotalAmount() {
        double totalAmount = order.getProducts().stream().mapToDouble(product -> product.getPrice()).sum();
        double taxAmount = totalAmount * 0.21;

        return totalAmount + taxAmount;
    }
}
