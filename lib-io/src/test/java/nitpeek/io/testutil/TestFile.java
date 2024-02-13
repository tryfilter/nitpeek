package nitpeek.io.testutil;

import nitpeek.core.api.process.PageSource;
import nitpeek.core.impl.process.StringPageSource;

import java.util.List;

public final class TestFile {
    private TestFile() {
    }

    public static PageSource getContent() {
        var pages =  List.of(
                """
                        HEADER Text
                        Main Heading
                        Some simple test in a paragraph.
                        Next line.
                        Skipped 2 lines.
                        Italicized text.
                        Some special characters: üÄß^°ä#`'”
                        “Quote”
                        “”
                        «Other quote»
                        Footnote:1 another footnote: 2
                        And a third footnote3
                        1 Footnote 1
                        2 Second footnote
                        3 This is the last one
                        Footer
                        Lines
                        3 total
                        Page 1/3""",
                """
                        HEADER Text
                        Single-Line Page
                        Footer
                        Lines
                        3 total
                        Page 2/3""",
                """
                        HEADER Text
                        Following this line there will be three more lines.
                        Second line Different Font ending in a hyphen-
                        ated word. Technically the third line contains part of it as well
                        Special string: (marker)
                        Footer
                        Lines
                        3 total
                        Page 3/3"""
        );

        return new StringPageSource(pages);
    }
}
