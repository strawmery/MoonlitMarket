package dev.maria.moonlitmarket.Products;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping(path = "/products")
public class ProductsController {

    private ProductsService service;

    public ProductsController(ProductsService service){
        this.service = service;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/addproduct")
    public ResponseEntity<Products> addProduct(@RequestBody Products product) {
        Products createdProduct = service.addProducts(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/public/list")
    public ResponseEntity<List<Products>> listProducts() {
        List<Products> products = service.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/public/listbyid/{id}")
    public ResponseEntity<Optional<Products>> getProductById(@PathVariable Long id) {
        Optional<Products> product = service.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        if(service.getProductById(id).isPresent()){
            service.deleteProducts(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<Products> putMethodName(@PathVariable Long id, @RequestBody Products details) {
        try {
            Products updatedProducts = service.updateProduct(id, details);
            return ResponseEntity.ok(updatedProducts);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
