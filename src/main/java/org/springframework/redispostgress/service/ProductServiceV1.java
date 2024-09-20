package org.springframework.redispostgress.service;

import lombok.AllArgsConstructor;
import org.springframework.redispostgress.entity.Product;
import org.springframework.redispostgress.rpository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProductServiceV1 {

    private ProductRepository productRepository;

    public Mono<Product> getProduct(int id) {
        return productRepository.findById(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return productRepository.findById(id)
                .flatMap(p -> productMono.doOnNext(pr -> pr.setId(id)))
                .flatMap(p -> productRepository.save(p));
    }


}
