package org.springframework.redispostgress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.redispostgress.entity.Product;
import org.springframework.redispostgress.service.util.CacheTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {

    @Autowired
    private CacheTemplate<Integer, Product> cache;

    public Mono<Product> getProduct(int id) {
        return cache.get(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> product) {
        return product.flatMap(p -> this.cache.update(id, p));
    }

    public Mono<Void> deleteProduct(int id) {
        return cache.delete(id);
    }

}
