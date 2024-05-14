package nitpeek.core.api.config;

import java.util.Optional;

/**
 * Allows retrieving dependencies of arbitrary type.
 * This interface loosely follows the intent of the service locator pattern.
 */
public interface Configuration {
    /**
     * @return an Optional wrapping the dependency of the specified type if present, an empty Optional otherwise.
     */
    <T> Optional<T> get(Class<T> configurationType);
}