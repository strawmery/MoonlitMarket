package dev.maria.moonlitmarket.CategoryTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import dev.maria.moonlitmarket.Category.Category;
import dev.maria.moonlitmarket.Category.CategoryController;
import dev.maria.moonlitmarket.Category.CategoryDTO;
import dev.maria.moonlitmarket.Category.CategoryService;


@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @InjectMocks
    private CategoryController controller;

    @Mock
    private CategoryService service;

    private Category sampleCategory;
    private CategoryDTO sampleCategoryDTO;

    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        sampleCategoryDTO = new CategoryDTO();
        sampleCategoryDTO.setId(1L);
        sampleCategoryDTO.setName("Electronics");
    }

        @Test
    @WithMockUser(authorities = "ADMIN")
    void testAddCategory() throws Exception {
        when(service.addCategory(any(CategoryDTO.class))).thenReturn(sampleCategory);
        when(service.toDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        ResponseEntity<CategoryDTO> response = controller.addCategory(sampleCategoryDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleCategoryDTO.getId(), response.getBody().getId());
        verify(service).addCategory(any(CategoryDTO.class));
    }

    @Test
    void testListCategories() throws Exception {
        List<Category> categoriesList = List.of(sampleCategory);
        List<CategoryDTO> categoryDTOs = List.of(sampleCategoryDTO);

        when(service.getAllCategories()).thenReturn(categoriesList);

        ResponseEntity<List<CategoryDTO>> response = controller.listCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(service).getAllCategories();
    }

    @Test
    void testGetCategoryById() throws Exception {
        when(service.getCategoryById(1L)).thenReturn(Optional.of(sampleCategory));
        when(service.toDTO(sampleCategory)).thenReturn(sampleCategoryDTO);

        ResponseEntity<CategoryDTO> response = controller.getCategoryById(1L);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(sampleCategoryDTO.getId(), response.getBody().getId());
        verify(service).getCategoryById(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteCategory() throws Exception {
        when(service.getCategoryById(1L)).thenReturn(Optional.of(sampleCategory));
        doNothing().when(service).deleteCategory(1L);

        ResponseEntity<Void> response = controller.deleteCategory(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).getCategoryById(1L);
        verify(service).deleteCategory(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteCategoryDoesNotExist() throws Exception {
        when(service.getCategoryById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteCategory(1L);

        verify(service).getCategoryById(1L);
        verify(service, never()).deleteCategory(anyLong());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateCategory() throws Exception {
        String updatedName = "Updated Electronics";
        Category updatedCategory = new Category();
        updatedCategory.setName(updatedName);
        CategoryDTO updatedCategoryDTO = new CategoryDTO();
        updatedCategoryDTO.setName(updatedName);

        when(service.updateCategory(eq(1L), eq(updatedName))).thenReturn(updatedCategory);
        when(service.toDTO(updatedCategory)).thenReturn(updatedCategoryDTO);

        ResponseEntity<CategoryDTO> response = controller.updateCategory(1L, updatedName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedCategoryDTO.getName(), response.getBody().getName());
        verify(service).updateCategory(eq(1L), eq(updatedName));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateCategoryDoesNotExist() throws Exception {
        String updatedName = "Updated Electronics";
        when(service.updateCategory(eq(1L), eq(updatedName)))
                .thenThrow(new RuntimeException("Category not found"));

        ResponseEntity<CategoryDTO> response = controller.updateCategory(1L, updatedName);
    
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service).updateCategory(eq(1L), eq(updatedName));
    }
}






