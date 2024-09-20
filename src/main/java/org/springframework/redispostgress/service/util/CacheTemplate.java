package org.springframework.redispostgress.service.util;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * An abstract cache template service designed to handle generic caching operations for entities.
 * <p>
 * This class provides a framework for implementing caching strategies by defining methods for
 * retrieving, updating, deleting, and inserting entities from both the cache and the source storage.
 * Subclasses are expected to implement the abstract methods to provide concrete implementations
 * based on the actual caching mechanism and data source being used.
 */
@Service
public abstract class CacheTemplate<KEY, ENTITY> {


    /**
     * Retrieves an entity from the cache. If the entity is not present in the cache,
     * it attempts to fetch the entity from the source, updates the cache with the fetched entity,
     * and then returns the entity.
     *
     * @param key The unique identifier for the entity to retrieve.
     * @return A {@link Mono} that emits the retrieved entity.
     */
    public Mono<ENTITY> get(KEY key) {
        return getFromCache(key)
                .switchIfEmpty(
                        getFromSource(key)
                                .flatMap(e -> updateAllFromCache(key, e))
                );
    }

    /**
     * Updates an entity in the source storage and removes it from the cache.
     * After successfully updating the source, it deletes the entity from the cache.
     * Instead of deleting it from cache, you can update it there too.
     *
     * @param key    The unique identifier for the entity to update.
     * @param entity The updated entity object.
     * @return A {@link Mono} that completes upon successful deletion from the cache.
     */
    public Mono<ENTITY> update(KEY key, ENTITY entity) {
        return updateSource(key, entity)
                .flatMap(e -> deleteAllFromCache(key).thenReturn(e));
    }

    /**
     * Deletes an entity from the source storage and removes it from the cache.
     * After successfully deleting the entity from the source, it also deletes the entity from the cache.
     *
     * @param key The unique identifier for the entity to delete.
     * @return A {@link Mono} that completes upon successful deletion from both the source and the cache.
     */
    public Mono<Void> delete(KEY key) {
        return deleteSource(key)
                .then(deleteAllFromCache(key));
    }


    abstract protected Mono<ENTITY> getFromSource(KEY key);

    abstract protected Mono<ENTITY> getFromCache(KEY key);

    abstract protected Mono<ENTITY> updateSource(KEY key, ENTITY entity);

    abstract protected Mono<ENTITY> updateAllFromCache(KEY key, ENTITY entity);

    abstract protected Mono<Void> deleteSource(KEY key);

    abstract protected Mono<Void> deleteAllFromCache(KEY key);


}
