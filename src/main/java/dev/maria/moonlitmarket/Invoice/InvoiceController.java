package dev.maria.moonlitmarket.Invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
