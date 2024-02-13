package nitpeek.test.mockito;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.mockito.Mockito.mock;

/**
 * Creates a mock before any unit tests annotated with {@code @ExtendWith(MockitoInitialized.class)} have run.<br>
 * This reduces the combined time of test classes using Mockito.<br>
 * The exact mechanism by which this speedup occurs is not clear, but the performance gain is undeniable:
 * at the time of introducing this workaround, the entire test suite went from a runtime of 7.5s down to 4.8s.
 */
public final class MockitoInitialized implements BeforeAllCallback {
    private static Object init;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (init == null) init = mock(Object.class);
    }
}
