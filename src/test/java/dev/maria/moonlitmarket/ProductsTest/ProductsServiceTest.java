package dev.maria.moonlitmarket.ProductsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Category.CategoryService;
import dev.maria.moonlitmarket.Products.Products;
import dev.maria.moonlitmarket.Products.ProductsDTO;
import dev.maria.moonlitmarket.Products.ProductsRepository;
import dev.maria.moonlitmarket.Products.ProductsService;

public class ProductsServiceTest {

    @Mock
    private ProductsRepository repository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProducts() {
        // Arrange
        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        ProductsDTO productsDTO = new ProductsDTO();
        productsDTO.setName("Smartphone");
        productsDTO.setDescription("Latest model smartphone");
        productsDTO.setPrice(999.99);
        productsDTO.setSize("Medium");
        productsDTO.setCategoryName("Electronics");

        Products productToAdd = Products.builder()
                .name("Smartphone")
                .description("Latest model smartphone")
                .price(999.99)
                .size("Medium")
                .category(category)
                .build();

        Products savedProduct = Products.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest model smartphone")
                .price(999.99)
                .size("Medium")
                .category(category)
                .build();

        when(categoryService.findByName("Electronics")).thenReturn(category);
        when(repository.save(productToAdd)).thenReturn(savedProduct);

        // Act
        ProductsDTO result = service.addProducts(productsDTO);

        // Assert
        verify(repository).save(productToAdd);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Smartphone", result.getName());
        assertEquals("Latest model smartphone", result.getDescription());
        assertEquals(999.99, result.getPrice(), 0.001);
        assertEquals("Medium", result.getSize());
        assertEquals("Electronics", result.getCategoryName());
    }

    @Test
    void deleteProductsTest() {
        Long productId = 1L;
        when(repository.existsById(productId)).thenReturn(true);

        // Act
        service.deleteProducts(productId);

        // Assert
        verify(repository).existsById(productId);
        verify(repository).deleteById(productId);
    }

    @Test
    void testDeleteProducts_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 1L;
        when(repository.existsById(productId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.deleteProducts(productId);
        });
        assertEquals("Producto no encontrado", exception.getMessage());

        verify(repository).existsById(productId);
        verify(repository, never()).deleteById(productId);
    }

    @Test
    void getAllProductsTest() {
        List<Products> productList = List.of(
            Products.builder()
                    .id(1L)
                    .name("Smartphone")
                    .description("Latest model smartphone")
                    .price(999.99)
                    .size("Medium")
                    .category(Category.builder().id(1L).name("Electronics").build())
                    .build(),
            Products.builder()
                    .id(2L)
                    .name("Laptop")
                    .description("High-performance laptop")
                    .price(1299.99)
                    .size("Large")
                    .category(Category.builder().id(1L).name("Electronics").build())
                    .build()
        );

        when(repository.findAll()).thenReturn(productList);

        // Act
        List<ProductsDTO> result = service.getAllProducts();
    
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void testGetProductById() {
            // Arrange
            Long productId = 1L;
            ProductsDTO productDTO = new ProductsDTO();
            productDTO.setId(productId);
            productDTO.setName("Smartphone");
            productDTO.setDescription("Latest model smartphone");
            productDTO.setPrice(999.99);
            productDTO.setSize("Medium");
            productDTO.setCategoryName("Electronics");

            Products product = Products.builder()
                    .id(productId)
                    .name("Smartphone")
                    .description("Latest model smartphone")
                    .price(999.99)
                    .size("Medium")
                    .category(Category.builder().id(1L).name("Electronics").build())
                    .build();

            when(repository.findById(productId)).thenReturn(Optional.of(product));

            // Act
            Optional<ProductsDTO> result = service.getProductById(productId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(productDTO, result.get());
            verify(repository).findById(productId);
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
    
        Products existingProduct = Products.builder()
                .id(productId)
                .name("Smartphone")
                .description("Old model smartphone")
                .price(799.99)
                .size("Medium")
                .category(Category.builder().id(1L).name("Electronics").build())
                .build();
    
        ProductsDTO updatedDetails = new ProductsDTO();
        updatedDetails.setName("Smartphone");
        updatedDetails.setDescription("Latest model smartphone");
        updatedDetails.setPrice(999.99);
        updatedDetails.setSize("Large");
        updatedDetails.setCategoryName("Gadgets");

        Products updatedProduct = Products.builder()
                .id(productId)
                .name("Smartphone")
                .description("Latest model smartphone")
                .price(999.99)
                .size("Large")
                .category(Category.builder().id(2L).name("Gadgets").build())
                .build();

        when(repository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryService.findByName("Gadgets")).thenReturn(updatedProduct.getCategory());
        when(repository.save(existingProduct)).thenReturn(updatedProduct);

        // Act
        ProductsDTO result = service.updateProduct(productId, updatedDetails);
    
        // Assert
        assertNotNull(result);
        assertEquals("Latest model smartphone", result.getDescription());
        assertEquals(999.99, result.getPrice(), 0.001);
        assertEquals("Large", result.getSize());
        assertEquals("Gadgets", result.getCategoryName());
        verify(repository).findById(productId);
        verify(repository).save(existingProduct);
    }

    @Test
    void testUpdateProductNotExist() {
        // Arrange
        Long productId = 1L;

        ProductsDTO updatedDetails = new ProductsDTO();
        updatedDetails.setName("Smartphone");
        updatedDetails.setDescription("Latest model smartphone");
        updatedDetails.setPrice(999.99);
        updatedDetails.setSize("Large");
        updatedDetails.setCategoryName("Gadgets");

        when(repository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.updateProduct(productId, updatedDetails);
        });

        assertEquals("Producto no encontrado", exception.getMessage());
        verify(repository).findById(productId);
        verify(repository, never()).save(any());
    }
}

