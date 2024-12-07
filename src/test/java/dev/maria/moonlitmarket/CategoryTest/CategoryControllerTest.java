package dev.maria.moonlitmarket.CategoryTest;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Category.CategoryService;
import dev.maria.moonlitmarket.Category.CategoryController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
    }

    @Test
    void testAddCategory() {
        when(categoryService.addCategory(any(Category.class))).thenReturn(category);

        ResponseEntity<Category> response = categoryController.addCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Electronics", response.getBody().getName());

        verify(categoryService, times(1)).addCategory(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.of(category));
        doNothing().when(categoryService).deleteCategory(category.getId());

        ResponseEntity<Void> response = categoryController.deleteCategory(category.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(categoryService, times(1)).deleteCategory(category.getId());
    }
    
    @Test
    void testUpdateCategory() {
        String newName = "Home Appliances";
        Category updatedCategory = new Category(1L, newName, null);
        when(categoryService.updateCategory(category.getId(), newName)).thenReturn(updatedCategory);

        ResponseEntity<Category> response = categoryController.updateCategory(category.getId(), newName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newName, response.getBody().getName());

        verify(categoryService, times(1)).updateCategory(category.getId(), newName);
    }

    @Test
    void testUpdateCategoryNotFound() {
        String newName = "Home Appliances";
        when(categoryService.updateCategory(category.getId(), newName)).thenThrow(new RuntimeException("Category not found"));

        ResponseEntity<Category> response = categoryController.updateCategory(category.getId(), newName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(categoryService, times(1)).updateCategory(category.getId(), newName);
    }

    @Test
    void testListCategories() {
        List<Category> categories = List.of(category);
        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.listCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Electronics", response.getBody().get(0).getName());

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void testGetCategoryById() {
        when(categoryService.getCategoryById(category.getId())).thenReturn(Optional.of(category));

        ResponseEntity<Optional<Category>> response = categoryController.getCategoryById(category.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals("Electronics", response.getBody().get().getName());

        verify(categoryService, times(1)).getCategoryById(category.getId());
    }
}


