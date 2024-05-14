package nitpeek.core.impl.config;

import nitpeek.core.api.config.Configuration;

import java.util.Map;
import java.util.Optional;

public final class MapConfiguration implements Configuration {

    private final Map<Class<?>, ?> config;

    public MapConfiguration(Map<Class<?>, ?> config) {
        this.config = Map.copyOf(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(Class<T> configurationType) {
        var storedValue = config.get(configurationType);

        if (configurationType.isInstance(storedValue)) {
            // This is the line that generates an "unchecked" warning. The instance check above ensures that storedValue
            // can always be cast to T since T is guaranteed to be a supertype of the runtime type of storedValue
            return Optional.of((T) storedValue);
        }

        return Optional.empty();
    }
}