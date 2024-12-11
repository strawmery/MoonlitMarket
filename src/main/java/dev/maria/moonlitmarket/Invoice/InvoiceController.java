package dev.maria.moonlitmarket.Invoice;

import java.net.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
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
        byte[] pdf = invoiceService.dowloadInvoice(orderId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
