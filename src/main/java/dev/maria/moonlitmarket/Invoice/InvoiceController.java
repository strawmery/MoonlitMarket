package dev.maria.moonlitmarket.Invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @PostMapping("/user/invoice/{OrderId}")
    public ResponseEntity<Invoice> createInvoice(@PathVariable Long OrderId){
        try{
            Invoice invoice = invoiceService.createInvoice(OrderId);
            return ResponseEntity.ok(invoice);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadInvoice(@RequestParam Long orderId) {
        try {
            byte[] pdf = invoiceService.dowloadInvoice(orderId);
            logger.info("Generated invoice for orderId: {}", orderId);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            logger.error("Error generating invoice for orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
