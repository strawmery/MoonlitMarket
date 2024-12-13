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

    public ProductsDTO addProducts(ProductsDTO productsDTO) {
        Category category = categoryService.findById(productsDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Products product = new Products();
        product.setName(productsDTO.getName());
        product.setDescription(productsDTO.getDescription());
        product.setPrice(productsDTO.getPrice());
        product.setSize(productsDTO.getSize());
        product.setCategory(category);

        Products savedProduct = repository.save(product);

        return toDTO(savedProduct);
    }

    public void deleteProducts(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Producto no encontrado");
        }
    }

    public List<ProductsDTO> getAllProducts() {
        List<Products> products = repository.findAll();
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductsDTO> getProductById(long id) {
        return repository.findById(id).map(this::toDTO);
    }

    public ProductsDTO updateProduct(Long id, ProductsDTO productsDTO) {
        Category category = categoryService.findById(productsDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

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

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }
        return dto;
    }
}


