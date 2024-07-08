package com.nitpeek.core.impl.config;

import com.nitpeek.core.api.config.Configuration;
import com.nitpeek.core.api.config.Context;

import java.util.HashMap;
import java.util.Map;

public final class SimpleContext implements Context {
    private final Map<Class<?>, Object> configMap = new HashMap<>();

    @Override
    public Configuration getConfiguration() {
        return new MapConfiguration(configMap);
    }

    @Override
    public <T> void registerWithOverwrite(T newValue, Class<? super T> type) {
        configMap.put(type, newValue);
    }

    @Override
    public <T> void registerIfNotExists(T newValue, Class<? super T> type) {
        configMap.putIfAbsent(type, newValue);
    }
}