package io.quarkus.redis.datasource.impl;

import static io.smallrye.mutiny.helpers.ParameterValidation.nonNull;

import java.util.List;

import io.quarkus.redis.datasource.api.SortArgs;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Response;

public class ReactiveSortable<K, V> extends AbstractRedisCommands {

    private final Class<V> typeOfValue;

    private static final SortArgs DEFAULT_INSTANCE = new SortArgs();

    public ReactiveSortable(RedisCommandExecutor redis, Marshaller marshaller, Class<V> typeOfValue) {
        super(redis, marshaller);
        this.typeOfValue = typeOfValue;
    }

    public Uni<List<V>> sort(K key) {
        return sort(key, DEFAULT_INSTANCE);
    }

    public Uni<List<V>> sort(K key, SortArgs sortArguments) {
        nonNull(key, "key");
        nonNull(sortArguments, "sortArguments");
        return execute(RedisCommand.of(Command.SORT).put(marshaller.encode(key)).putArgs(sortArguments))
                .map(r -> marshaller.decodeAsList(r, typeOfValue));
    }

    public Uni<Long> sortAndStore(K key, K destination, SortArgs args) {
        nonNull(key, "key");
        nonNull(destination, "destination");
        nonNull(args, "args");
        RedisCommand cmd = RedisCommand.of(Command.SORT)
                .put(marshaller.encode(key))
                .putAll(args.toArgs())
                .put("STORE")
                .put(marshaller.encode(destination));
        return execute(cmd)
                .map(Response::toLong);
    }

    public Uni<Long> sortAndStore(K key, K destination) {
        return sortAndStore(key, destination, DEFAULT_INSTANCE);
    }
}
