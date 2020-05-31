package com;

import java.util.HashMap;

public class VariableCache {

    private static VariableCache instance;
    private HashMap<String, String> cache;

    private VariableCache() {
        this.cache = new HashMap<>();
    }

    public void set(String name, String value) {
        this.cache.put(name, value);
    }

    public String get(String name) {
        return this.cache.get(name);
    }

    public void clear() {
        this.cache = new HashMap<>();
    }

    public HashMap<String, String> getCache() {
        return this.cache;
    }

    public static VariableCache getInstance() {
        if (instance == null) {
            instance = new VariableCache();
        }
        return instance;
    }
}
