package org.springframework.redispostgress.rpository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.redispostgress.entity.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {


}
