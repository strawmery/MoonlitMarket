package dev.maria.moonlitmarket.Invoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateInvoice_Success() throws Exception {
        Long orderId = 1001L;
        Invoice mockInvoice = new Invoice();
        mockInvoice.setInvoiceNumber("INV-1001-12345");
        mockInvoice.setTotalAmount(80.0);
        mockInvoice.setTaxAmount(16.8);
        mockInvoice.setStatus("pending");

        Mockito.when(invoiceService.createInvoice(orderId)).thenReturn(mockInvoice);

        mockMvc.perform(post("/api/user/invoice/{OrderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-1001-12345"))
                .andExpect(jsonPath("$.totalAmount").value(80.0))
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    void testCreateInvoice_BadRequest() throws Exception {
        Long orderId = 999L;

        Mockito.when(invoiceService.createInvoice(orderId)).thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(post("/api/user/invoice/{OrderId}", orderId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadInvoice_Success() throws Exception {
        Long orderId = 1001L;
        byte[] mockPdf = new byte[]{1, 2, 3, 4};

        Mockito.when(invoiceService.dowloadInvoice(orderId)).thenReturn(mockPdf);

        mockMvc.perform(get("/api/public/download").param("orderId", String.valueOf(orderId)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=invoice_1001.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(mockPdf));
    }

    @Test
    void testDownloadInvoice_InternalServerError() throws Exception {
        Long orderId = 999L;

        Mockito.when(invoiceService.dowloadInvoice(orderId)).thenThrow(new RuntimeException("Error generating invoice"));

        mockMvc.perform(get("/api/public/download").param("orderId", String.valueOf(orderId)))
                .andExpect(status().isInternalServerError());
    }
}

