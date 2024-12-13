package dev.maria.moonlitmarket.ProductsTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Category.CategoryService;
import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Products.ProductsDTO;
import dev.maria.moonlitmarket.Products.ProductsRepository;
import dev.maria.moonlitmarket.Products.ProductsService;


@SpringBootTest
public class ProductsServiceTest {

    @Mock
    private ProductsRepository repository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductsService service;

    private ProductsDTO productDTO;
    private Category category;
    private Products product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        category = new Category(1L, "Electronics", new ArrayList<>());
        productDTO = new ProductsDTO(1L, "Product Name", "Product Description", 100.0, "M", category.getId());
        product = new Products(1L, "Product Name", "Product Description", 100.0, "M", category, null);
    }

    @Test
    public void addProduct_ValidProduct_ReturnsProductDTO() {
        when(categoryService.findById(anyLong())).thenReturn(Optional.of(category));
        when(repository.save(any(Products.class))).thenReturn(product);

        ProductsDTO result = service.addProducts(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getDescription(), result.getDescription());
        assertEquals(productDTO.getPrice(), result.getPrice());
        assertEquals(productDTO.getSize(), result.getSize());
        assertEquals(productDTO.getCategoryId(), result.getCategoryId());
    }

    @Test
    public void addProduct_CategoryNotFound_ThrowsException() {
        when(categoryService.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.addProducts(productDTO);
        });

        assertEquals("Categoría no encontrada", thrown.getMessage());
    }

    @Test
    public void deleteProduct_ProductExists_DeletesProduct() {
        when(repository.existsById(anyLong())).thenReturn(true);
        doNothing().when(repository).deleteById(anyLong());

        service.deleteProducts(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteProduct_ProductNotFound_ThrowsException() {
        when(repository.existsById(anyLong())).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.deleteProducts(1L);
        });

        assertEquals("Producto no encontrado", thrown.getMessage());
    }

    @Test
    public void getAllProducts_ReturnsProductList() {
        when(repository.findAll()).thenReturn(Collections.singletonList(product));

        List<ProductsDTO> result = service.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDTO.getName(), result.get(0).getName());
    }

    @Test
    public void getProductById_ValidId_ReturnsProductDTO() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(product));

        Optional<ProductsDTO> result = service.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(productDTO.getName(), result.get().getName());
    }

    @Test
    public void getProductById_ProductNotFound_ReturnsEmpty() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ProductsDTO> result = service.getProductById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    public void updateProduct_ValidProduct_ReturnsUpdatedProductDTO() {
        when(categoryService.findById(anyLong())).thenReturn(Optional.of(category));
        when(repository.findById(anyLong())).thenReturn(Optional.of(product));
        when(repository.save(any(Products.class))).thenReturn(product);

        ProductsDTO updatedProduct = service.updateProduct(1L, productDTO);

        assertNotNull(updatedProduct);
        assertEquals(productDTO.getName(), updatedProduct.getName());
        assertEquals(productDTO.getDescription(), updatedProduct.getDescription());
        assertEquals(productDTO.getPrice(), updatedProduct.getPrice());
        assertEquals(productDTO.getSize(), updatedProduct.getSize());
    }

    @Test
    public void updateProduct_CategoryNotFound_ThrowsException() {
        when(categoryService.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.updateProduct(1L, productDTO);
        });

        assertEquals("Categoría no encontrada", thrown.getMessage());
    }

    @Test
    public void updateProduct_ProductNotFound_ThrowsException() {
        when(categoryService.findById(anyLong())).thenReturn(Optional.of(category));
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.updateProduct(1L, productDTO);
        });

        assertEquals("Producto no encontrado", thrown.getMessage());
    }
}


