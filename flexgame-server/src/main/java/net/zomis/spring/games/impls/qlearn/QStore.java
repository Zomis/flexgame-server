package net.zomis.spring.games.impls.qlearn;

public interface QStore<S> {

    double getOrDefault(S key, double defaultValue);
    void put(S ket, double value);
    long size();

}
