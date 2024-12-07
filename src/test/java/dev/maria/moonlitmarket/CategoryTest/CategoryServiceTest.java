package dev.maria.moonlitmarket.CategoryTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Category.CategoryRepository;
import dev.maria.moonlitmarket.Category.CategoryService;

import java.util.List;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Electronics");
    }

    @Test
    void testAddCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.addCategory(category);
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testDeleteCategory() {
        category.setId(1L);

        when(categoryRepository.existsById(category.getId())).thenReturn(true);

        categoryService.deleteCategory(category.getId());
        verify(categoryRepository, times(1)).deleteById(category.getId());
    }


    @Test
    void testDeleteCategoryNotFound() {
        category.setId(1L);

        when(categoryRepository.existsById(category.getId())).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(category.getId());
        });
        assertEquals("Categoria no encontrada", exception.getMessage());
    }


    @Test
    void testGetCategoryById() {
        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(category.getId());
        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(categoryRepository.existsById(category.getId())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(category.getId());
        });
        assertEquals("Categoria no encontrada", exception.getMessage());
    }

    @Test
    void testGetAllCategories() {
        Category category2 = new Category();
        category2.setName("Clothing");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category, category2));

        List<Category> result = categoryService.getAllCategories();
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Clothing", result.get(1).getName());
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category updatedCategory = categoryService.updateCategory(category.getId(), "New Name");
        assertNotNull(updatedCategory);
        assertEquals("New Name", updatedCategory.getName());
    }

    @Test
    void testUpdateCategoryNotFound() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(category.getId(), "New Name");
        });
        assertEquals("user not found with the id :" + category.getId(), exception.getMessage());
    }
}

