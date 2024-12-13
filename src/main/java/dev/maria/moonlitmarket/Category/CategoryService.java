package dev.maria.moonlitmarket.Category;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.maria.moonlitmarket.Products.Products;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public Optional<Category> findById(Long id) {
        return repository.findById(id);
    }

    public Category addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return repository.save(category);
    }

    public void deleteCategory(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("Categoría no encontrada");
        }
    }

    public Optional<Category> getCategoryById(Long id) {
        if(repository.existsById(id)){
            return repository.findById(id);
        }else{
            throw new RuntimeException("Categoria no encontrada");
        }
    }
    
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category updateCategory(Long id, String name) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setName(name);
        return repository.save(category);
    }

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        List<Long> productsIds = category.getProducts() != null ?
                category.getProducts().stream()
                        .map(Products::getId)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return new CategoryDTO(category.getId(), category.getName(), productsIds);
    }
}