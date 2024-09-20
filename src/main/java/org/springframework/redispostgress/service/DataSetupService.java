package org.springframework.redispostgress.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.redispostgress.entity.Product;
import org.springframework.redispostgress.rpository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class DataSetupService implements CommandLineRunner {

    private ProductRepository productRepository;
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    private ApplicationContext applicationContext;


    @Override
    public void run(String... args) throws Exception {
        Resource resource = applicationContext.getResource("classpath:schema.sql"); // Load the resource
        String query = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(query);

        Mono<Void> insert = Flux.range(1, 1000)
                .map(i -> new Product(null, "product" + i, ThreadLocalRandom.current().nextInt(1, 100)))
                .collectList()
                .flatMapMany(l -> productRepository.saveAll(l))
                .then();
        this.r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .then()
                .then(insert)
                .doFinally(s -> System.out.println("data setup done " + s))
                .subscribe();
    }
}
