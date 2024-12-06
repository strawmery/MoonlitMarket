package dev.maria.moonlitmarket.Category;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public Category addCategory(Category category){
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

}

