package nitpeek.core.api.config;

/**
 * Represents a context into which dependencies can be injected (registered) and from which these dependencies can be
 * retrieved in the form of a configuration object.
 */
public interface Context {

    /**
     * @return a snapshot of the configuration associated with this context. Subsequent registrations to the context
     * are not reflected in previously returned configurations
     */
    Configuration getConfiguration();

    /**
     * Register a new object for a particular type <br>
     * If a value was already associated with that type, it gets overwritten.
     */
    <T> void registerWithOverwrite(T newValue, Class<? super T> type);

    /**
     * Register a new object for a particular type, unless a value was already associated with that type,
     * in which case the new mapping is ignored.
     */
    <T> void registerIfNotExists(T newValue, Class<? super T> type);
}