package net.zomis.spring.games.impls.qlearn;

import java.util.HashMap;
import java.util.Map;

public class QStoreMap<S> implements QStore<S> {

    private final Map<S, Double> map = new HashMap<>();

    @Override
    public double getOrDefault(Object key, double defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public void put(S key, double value) {
        map.put(key, value);
    }

    @Override
    public long size() {
        return map.size();
    }
}
