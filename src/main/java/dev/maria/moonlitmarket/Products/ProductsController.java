package dev.maria.moonlitmarket.Products;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping(path = "/api")
public class ProductsController {

    @Autowired
    private ProductsService service;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/addproduct")
    public ResponseEntity<ProductsDTO> addProduct(@RequestBody ProductsDTO product) {
        ProductsDTO createdProduct = service.addProducts(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/public/products/list")
    public ResponseEntity<List<ProductsDTO>> listProducts() {
        List<ProductsDTO> products = service.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/public/products/listbyid/{id}")
    public ResponseEntity<Optional<ProductsDTO>> getProductById(@PathVariable Long id) {
        Optional<ProductsDTO> product = service.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/products/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (service.getProductById(id).isPresent()) {
            service.deleteProducts(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/products/update/{id}")
    public ResponseEntity<ProductsDTO> updateProduct(@PathVariable Long id, @RequestBody ProductsDTO details) {
        try {
            ProductsDTO updatedProducts = service.updateProduct(id, details);
            return ResponseEntity.ok(updatedProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

