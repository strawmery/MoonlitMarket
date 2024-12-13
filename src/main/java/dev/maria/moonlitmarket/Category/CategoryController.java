package dev.maria.moonlitmarket.Category;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/category/addcategory")
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
        Category createdCategory = service.addCategory(categoryDTO);
        return new ResponseEntity<>(service.toDTO(createdCategory), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/category/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if(service.getCategoryById(id).isPresent()) {
            service.deleteCategory(id);
            return ResponseEntity.noContent().build();
        }else{
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
                return null;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/admin/category/update/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestParam String name) {
        try{
            Category updatedCategory = service.updateCategory(id, name);
            return ResponseEntity.ok(service.toDTO(updatedCategory));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/public/category/listcategories")
    public ResponseEntity<List<CategoryDTO>> listCategories() {
        try {
            List<CategoryDTO> categoryDTOs = service.getAllCategories().stream()
                    .map(service::toDTO)
                    .toList();
            return ResponseEntity.ok(categoryDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    

    @GetMapping("/public/category/listbyid/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = service.getCategoryById(id);
    
        if (category.isPresent()) {
            CategoryDTO categoryDTO = service.toDTO(category.get());
            return ResponseEntity.ok(categoryDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }    
}
