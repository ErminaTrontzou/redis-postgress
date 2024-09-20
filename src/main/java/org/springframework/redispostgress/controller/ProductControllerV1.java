package org.springframework.redispostgress.controller;

import org.springframework.redispostgress.entity.Product;
import org.springframework.redispostgress.service.ProductServiceV1;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product/v1")
public class ProductControllerV1 {

    private ProductServiceV1 productService;

    public ProductControllerV1(ProductServiceV1 productService) {
        this.productService = productService;
    }

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable Integer id, @RequestBody Mono<Product> product) {
        return productService.updateProduct(id, product);
    }
}
