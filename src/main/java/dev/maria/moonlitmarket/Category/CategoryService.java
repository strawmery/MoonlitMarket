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

    public Category findByName(String name){
        return repository.findByName(name).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }
    
    public Category addCategory(CategoryDTO categoryDTO){
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return repository.save(category);
    }

    public void deleteCategory(long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }else{
            throw new RuntimeException("Categoria no encontrada");
        }
    }

    public Optional<Category> getCategoryById(Long id){
        if(repository.existsById(id)){
            return repository.findById(id);
        }else{
            throw new RuntimeException("Categoria no encontrada");
        }
    }

    public List<Category> getAllCategories(){
        return repository.findAll();
    }

    public Category updateCategory(Long id, String name){
        Category category = repository.findById(id).orElse(null);
        if(category!=null){
            category.setName(name);
            return repository.save(category);
        }else{
            throw new RuntimeException("user not found with the id :"+id);
        }
    }

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        List<Long> productsIds = category.getProducts() != null? 
                    category.getProducts().stream()
                        .map(Products::getId)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return new CategoryDTO(category.getId(), category.getName(),productsIds);
    }

}

