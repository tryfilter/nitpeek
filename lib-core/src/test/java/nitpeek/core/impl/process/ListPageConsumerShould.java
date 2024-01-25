package nitpeek.core.impl.process;

import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.core.testutil.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ListPageConsumerShould {

    private static final List<TextPage> pages = List.of(
            new SimpleTextPage(List.of("test123"), 4),
            new SimpleTextPage(List.of("abracadabra"), 2),
            new SimpleTextPage(List.of("blue moon #67"), 67)
    );

    private ListPageConsumer consumer;

    @BeforeEach
    void setup() {
        consumer = new ListPageConsumer();
    }


    @Test
    void produceEmptyListWhenNoPagesConsumed() {
        consumer.finish();

        assertEquals(List.of(), consumer.getPages());
    }

    @Test
    void throwWhenPagesConsumedAfterFinishing() {
        consumer.finish();

        var page = TestUtil.emptyPage(5);
        assertThrows(IllegalStateException.class, () -> consumer.consumePage(page));
    }

    @Test
    void returnConsumedPagesWithFinish() {

        for (var page : pages) {
            consumer.consumePage(page);
        }

        consumer.finish();
        assertEquals(pages, consumer.getPages());
    }

    @Test
    void returnConsumedPagesWithoutFinish() {

        for (var page : pages) {
            consumer.consumePage(page);
        }

        assertEquals(pages, consumer.getPages());
    }
}
