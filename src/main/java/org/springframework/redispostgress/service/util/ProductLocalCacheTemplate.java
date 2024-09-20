package org.springframework.redispostgress.service.util;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.redispostgress.entity.Product;
import org.springframework.redispostgress.rpository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {

    @Autowired
    private ProductRepository productRepository;
    private RLocalCachedMap<Integer, Product> map;

    public ProductLocalCacheTemplate(RedissonClient client) {
        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);
        map = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class), mapOptions);
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return Mono.just(this.map.get(id));
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.productRepository.findById(id)
                .doOnNext(p -> product.setId(id)) //after it retrieves the product from the repository, it updates the products id to the given id.
                //this ensures that the product passed to the save operation has the correct id
                .flatMap(p -> this.productRepository.save(product));
    }

    //fastPut is used to emit the updated product object downstream.
    //This is necessary because fastPut does not return the updated value; it simply updates the cache.
    //Wrapping the call with thenReturn(product) ensures that the caller of getFromCache can receive the updated product immediately after it's cached.
    @Override
    protected Mono<Product> updateAllFromCache(Integer id, Product product) {
        return Mono.create(sink ->
                this.map.fastPutAsync(id, product)
                        .thenAccept(b -> sink.success(product))
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        })
        );
    }

    @Override
    protected Mono<Void> deleteSource(Integer id) {
        return this.productRepository.deleteById(id);
    }


    // fastRemove(id) is used to remove the cache entry associated with the given id.
    // then() is used to signal completion of the operation. Since fastRemove does not return a value, then() is used to chain further operations or signals completion to the subscriber.
    @Override
    protected Mono<Void> deleteAllFromCache(Integer id) {
        return Mono.create(sink ->
                this.map.fastRemoveAsync(id)
                        .thenAccept(b -> sink.success())
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        })
        );
    }
}
