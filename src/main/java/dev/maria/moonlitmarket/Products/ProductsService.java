package dev.maria.moonlitmarket.Products;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ProductsService {

    private ProductsRepository repository;

    public ProductsService (ProductsRepository repository){
        this.repository = repository;
    }

    //admin
    public Products addProducts (Products products){
        return repository.save(products);
    }

    //admin
    public void deleteProducts(Long id){
        if(repository.existsById(id)){
            repository.deleteById(id);
        }else{
            throw new RuntimeException("Producto no encontrado");
        }
    }

    //publico
    public List<Products> getAllProducts(){
        return repository.findAll();
    }

    //publico
    public Optional<Products> getProductById(long id){
        if(repository.existsById(id)){
            return repository.findById(id);
        }else{
            throw new RuntimeException("producto no encontrado");
        }
    }

    public Products updateProduct(Long id, Products details){
        Products products = repository.findById(id).orElse(null);
        if(products != null){
            products.setName(details.getName());
            products.setDescription(details.getDescription());
            products.setPrice(details.getPrice());
            products.setSize(details.getSize());
            products.setCategory(details.getCategory());
            return repository.save(products);
        }else{
            throw new RuntimeException("Producto no encontrado");
        }
    }


}
