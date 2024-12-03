package dev.maria.moonlitmarket.ProductsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Products.ProductsController;
import dev.maria.moonlitmarket.Products.ProductsService;

@ExtendWith(MockitoExtension.class)
public class ProductsControllerTest {

    @InjectMocks
    private ProductsController controller;

    @Mock
    private ProductsService service;

    private Products sampleProduct;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        sampleProduct = Products.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest model smartphone")
                .price(999.99)
                .size("Medium")
                .category(sampleCategory)
                .build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddProduct() {
        // Arrange
        when(service.addProducts(any(Products.class))).thenReturn(sampleProduct);

        // Act
        ResponseEntity<Products> response = controller.addProduct(sampleProduct);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleProduct.getId(), response.getBody().getId());
        verify(service).addProducts(any(Products.class));
    }

    @Test
    void testListProducts() {
        // Arrange
        List<Products> productsList = List.of(sampleProduct);
        when(service.getAllProducts()).thenReturn(productsList);
    
        // Act
        ResponseEntity<List<Products>> response = controller.listProducts();
    
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(service).getAllProducts();
    }

    @Test
    void testGetProductById() {
        // Arrange
        when(service.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        // Act
        ResponseEntity<Optional<Products>> response = controller.getProductById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());
        assertEquals(sampleProduct.getId(), response.getBody().get().getId());
        verify(service).getProductById(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteProduct() {
        // Arrange
        when(service.getProductById(1L)).thenReturn(Optional.of(sampleProduct));
        doNothing().when(service).deleteProducts(1L);

        // Act
        ResponseEntity<Void> response = controller.deleteProduct(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).getProductById(1L);
        verify(service).deleteProducts(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteProductDoesNotExist() {
        // Arrange
        when(service.getProductById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Void> response = controller.deleteProduct(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service).getProductById(1L);
        verify(service, never()).deleteProducts(anyLong());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateProduct() {
        // Arrange
        Products updatedDetails = Products.builder()
                .name("Updated Smartphone")
                .description("Updated description")
                .price(1099.99)
                .size("Large")
                .category(sampleCategory)
                .build();

        when(service.updateProduct(eq(1L), any(Products.class))).thenReturn(updatedDetails);

        // Act
        ResponseEntity<Products> response = controller.putMethodName(1L, updatedDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Smartphone", response.getBody().getName());
        verify(service).updateProduct(eq(1L), any(Products.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateProductDoesNotExist() {
        // Arrange
        Products updatedDetails = Products.builder()
                .name("Updated Smartphone")
                .description("Updated description")
                .price(1099.99)
                .size("Large")
                .category(sampleCategory)
                .build();

        when(service.updateProduct(eq(1L), any(Products.class))).thenThrow(new RuntimeException("Producto no encontrado"));

        // Act
        ResponseEntity<Products> response = controller.putMethodName(1L, updatedDetails);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service).updateProduct(eq(1L), any(Products.class));
    }
}
