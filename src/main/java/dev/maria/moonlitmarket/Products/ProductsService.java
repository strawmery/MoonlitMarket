package dev.maria.moonlitmarket.Products;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Category.CategoryService;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository repository;

    @Autowired
    private CategoryService categoryService; 

    // Admin
    public ProductsDTO addProducts(ProductsDTO productsDTO) {
        Category category = categoryService.findByName(productsDTO.getCategoryName());

        Products product = new Products();
        product.setName(productsDTO.getName());
        product.setDescription(productsDTO.getDescription());
        product.setPrice(productsDTO.getPrice());
        product.setSize(productsDTO.getSize());
        product.setCategory(category);

        Products savedProduct = repository.save(product);

        return toDTO(savedProduct);
    }

    // Admin
    public void deleteProducts(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Producto no encontrado");
        }
    }

    // Público
    public List<ProductsDTO> getAllProducts() {
        List<Products> products = repository.findAll();
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Público
    public Optional<ProductsDTO> getProductById(long id) {
        return repository.findById(id).map(this::toDTO);
    }
    

    // Admin
    public ProductsDTO updateProduct(Long id, ProductsDTO productsDTO) {
        Category category = categoryService.findByName(productsDTO.getCategoryName());
        Products existingProduct = repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existingProduct.setName(productsDTO.getName());
        existingProduct.setDescription(productsDTO.getDescription());
        existingProduct.setPrice(productsDTO.getPrice());
        existingProduct.setSize(productsDTO.getSize());
        existingProduct.setCategory(category); 

        Products updatedProduct = repository.save(existingProduct);

        return toDTO(updatedProduct);
    }

    private ProductsDTO toDTO(Products product) {
        ProductsDTO dto = new ProductsDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSize(product.getSize());
        
        if(product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }
}

