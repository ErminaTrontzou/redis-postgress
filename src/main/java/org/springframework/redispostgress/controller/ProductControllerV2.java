package org.springframework.redispostgress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.redispostgress.entity.Product;
import org.springframework.redispostgress.service.ProductServiceV2;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product/v2")
public class ProductControllerV2 {

    @Autowired
    private ProductServiceV2 productService;

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable Integer id, @RequestBody Mono<Product> product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }
}
