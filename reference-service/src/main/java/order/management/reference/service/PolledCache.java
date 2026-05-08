package order.management.reference.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PolledCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final Supplier<Collection<V>> valueSupplier;
    private final Function<V, K> keyExtractor;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PolledCache(Supplier<Collection<V>> valueSupplier, Function<V, K> keyExtractor) {
        this.valueSupplier = valueSupplier;
        this.keyExtractor = keyExtractor;

        executor.scheduleAtFixedRate(this::refreshCache, 0, 15, TimeUnit.SECONDS);
    }

    private void refreshCache() {
        Collection<V> values = valueSupplier.get();
        Map<K, V> newCache = values.stream()
                .collect(Collectors.toMap(keyExtractor, entity -> entity, (existing, replacement) -> replacement));
        cache.putAll(newCache);
    }

    public V get(K key) {
        return cache.get(key);
    }
}
