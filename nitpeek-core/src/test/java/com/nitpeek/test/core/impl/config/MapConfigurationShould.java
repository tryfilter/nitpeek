package com.nitpeek.test.core.impl.config;

import com.nitpeek.core.impl.config.MapConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class MapConfigurationShould {

    @Test
    void returnEmptyOptionalWhenMapEmpty() {
        var configuration = new MapConfiguration(Map.of());
        assertEquals(Optional.empty(), configuration.get(Object.class));
    }

    @Test
    void returnEmptyOptionalWhenNoTypeMatch() {
        var configuration = new MapConfiguration(Map.of(Number.class, 5));
        assertEquals(Optional.empty(), configuration.get(String.class));
    }

    @Test
    void returnEmptyOptionalWhenNoExactTypeMatchExtends() {
        var configuration = new MapConfiguration(Map.of(Number.class, 5));
        assertEquals(Optional.empty(), configuration.get(Integer.class));
    }

    @Test
    void returnEmptyOptionalWhenNoExactTypeMatchSuper() {
        var configuration = new MapConfiguration(Map.of(Number.class, 5));
        assertEquals(Optional.empty(), configuration.get(Object.class));
    }

    @Test
    void returnValueWhenExactTypeMatch() {
        var configuration = new MapConfiguration(Map.of(Number.class, 5));
        assertEquals(Optional.of(5), configuration.get(Number.class));
    }
}