package dev.maria.moonlitmarket.ProductsTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.maria.moonlitmarket.Products.ProductsController;
import dev.maria.moonlitmarket.Products.ProductsDTO;
import dev.maria.moonlitmarket.Products.ProductsService;

@WebMvcTest(ProductsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductsService productsService;

    private ProductsDTO productDTO;

    @BeforeEach
    public void setUp() {
        productDTO = new ProductsDTO(1L, "Product Name", "Product Description", 100.0, "M", 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addProduct_ValidProduct_ReturnsCreated() throws Exception {
        when(productsService.addProducts(any(ProductsDTO.class))).thenReturn(productDTO);

        mockMvc.perform(post("/api/admin/addproduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(productDTO.getName()))
                .andExpect(jsonPath("$.description").value(productDTO.getDescription()))
                .andExpect(jsonPath("$.price").value(productDTO.getPrice()));
    }

    @Test
    public void listProducts_ReturnsProductList() throws Exception {
        List<ProductsDTO> productsList = Collections.singletonList(productDTO);
        when(productsService.getAllProducts()).thenReturn(productsList);

        mockMvc.perform(get("/api/public/products/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(productDTO.getName()));
    }

    @Test
    public void getProductById_ValidId_ReturnsProduct() throws Exception {
        when(productsService.getProductById(1L)).thenReturn(Optional.of(productDTO));

        mockMvc.perform(get("/api/public/products/listbyid/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()));
    }

    @Test
    public void getProductById_ProductNotFound_ReturnsNotFound() throws Exception {
        when(productsService.getProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/public/products/listbyid/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteProduct_ValidId_ReturnsNoContent() throws Exception {
        when(productsService.getProductById(1L)).thenReturn(Optional.of(productDTO));

        mockMvc.perform(delete("/api/admin/products/delete/1"))
                .andExpect(status().isNoContent());

        verify(productsService, times(1)).deleteProducts(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteProduct_ProductNotFound_ReturnsNotFound() throws Exception {
        when(productsService.getProductById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/products/delete/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateProduct_ValidProduct_ReturnsUpdatedProduct() throws Exception {
        when(productsService.updateProduct(eq(1L), any(ProductsDTO.class))).thenReturn(productDTO);

        mockMvc.perform(put("/api/admin/products/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(productDTO.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateProduct_ProductNotFound_ReturnsNotFound() throws Exception {
        when(productsService.updateProduct(eq(1L), any(ProductsDTO.class))).thenThrow(new RuntimeException("Producto no encontrado"));

        mockMvc.perform(put("/api/admin/products/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productDTO)))
                .andExpect(status().isNotFound());
    }
}


