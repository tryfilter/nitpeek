package com.nitpeek.test.core.impl.config;

import com.nitpeek.core.impl.config.SimpleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SimpleContextShould {

    private SimpleContext context;

    @BeforeEach
    void setup() {
        context = new SimpleContext();
    }

    @Test
    void supportExactRegistration() {
        int value = 5;
        var type = Integer.class;
        context.registerIfNotExists(value, type);

        var config = context.getConfiguration();
        int valueRetrieved = config.get(type).orElse(-1);
        assertEquals(valueRetrieved, value);
    }

    @Test
    void supportSupertypeRegistration() {
        int value = 5;
        var superType = Number.class;
        context.registerIfNotExists(value, superType);

        var config = context.getConfiguration();
        Number valueRetrieved = config.get(superType).orElse(-1);
        assertEquals(valueRetrieved.intValue(), value);
    }

    @Test
    void notOverwriteExistingValueWithRegisterIfNotExists() {
        int initialValue = 5;
        var type = Integer.class;
        context.registerIfNotExists(initialValue, type);
        context.registerIfNotExists(42, type);
        int retrievedValue = context.getConfiguration().get(type).orElse(-1);
        assertEquals(initialValue, retrievedValue);
    }

    @Test
    void notOverwriteValueForSuperOfExistingClassWithRegisterIfNotExists() {
        int initialValue = 5;
        int newValue = 42;
        var type = Integer.class;
        var superType = Number.class;
        context.registerIfNotExists(initialValue, type);
        context.registerIfNotExists(newValue, superType);
        int retrievedValue = context.getConfiguration().get(type).orElse(-1);
        assertEquals(initialValue, retrievedValue);
    }

    @Test
    void overwriteExistingValueWithRegisterWithOverwrite() {
        int initialValue = 5;
        int newValue = 42;
        var type = Integer.class;
        context.registerIfNotExists(initialValue, type);
        context.registerWithOverwrite(newValue, type);
        int retrievedValue = context.getConfiguration().get(type).orElse(-1);
        assertEquals(newValue, retrievedValue);
    }

    @Test
    void notOverwriteValueForSuperOfExistingClassWithRegisterWithOverwrite() {
        int initialValue = 5;
        int newValue = 42;
        var type = Integer.class;
        var superType = Number.class;
        context.registerIfNotExists(initialValue, type);
        context.registerWithOverwrite(newValue, superType);
        int retrievedValue = context.getConfiguration().get(type).orElse(-1);
        assertEquals(initialValue, retrievedValue);
    }

    @Test
    void notOverwriteValueForSubOfExistingClassWithRegisterWithOverwrite() {
        int initialValue = 5;
        int newValue = 42;
        var type = Number.class;
        var subType = Integer.class;
        context.registerIfNotExists(initialValue, type);
        context.registerWithOverwrite(newValue, subType);
        int retrievedValue = context.getConfiguration().get(type).orElse(-1).intValue();
        assertEquals(initialValue, retrievedValue);
    }
}