package dev.maria.moonlitmarket.Orders;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.maria.moonlitmarket.Products.Products;

@RestController
@RequestMapping("/api")
public class OrdersController {

    @Autowired
    private OrdersService service;

    @PostMapping("/user/orders/create")
    public ResponseEntity<Orders> createOrder(@RequestParam Long userId, @RequestBody List<Products> products) {
        try{
            Orders order = service.createOrder(userId, products);
            return ResponseEntity.ok(order);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/user/orders/update/{orderId}")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long orderId, @RequestParam Status status){
        try{
            Orders order = service.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
