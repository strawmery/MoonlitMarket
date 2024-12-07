package dev.maria.moonlitmarket.Products;

import dev.maria.moonlitmarket.Category.CategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDTO {

    private Long id;

    private String name;

    private String description;

    private double price;

    private String size;

    private String categoryName;
}
