package nitpeek.core.api.process;

import nitpeek.core.api.analyze.TextPage;

public interface PageConsumer {
    void consumePage(TextPage page);
    void finish();
}
