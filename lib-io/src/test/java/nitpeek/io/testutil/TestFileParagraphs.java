package nitpeek.io.testutil;

import nitpeek.core.api.process.PageSource;
import nitpeek.core.impl.process.StringPageSource;

import java.util.List;

public final class TestFileParagraphs {
    private TestFileParagraphs() {
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
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. In nec dolor finibus, porta arcu sagittis, porttitor nulla. Donec mattis, tellus in ultrices pulvinar, dui turpis hendrerit quam, sit amet sollicitudin est tortor non lectus. Nulla semper nisl libero, a ornare velit cursus ac. Mauris ut suscipit ante. Phasellus vestibulum lorem tellus. Morbi mollis tellus et libero finibus efficitur. Vestibulum non lacinia mauris, ut fringilla velit. Sed laoreet nisi non maximus molestie. Aliquam convallis, purus at molestie congue, ipsum lorem dapibus mi, vel malesuada neque ex sit amet erat. Suspendisse sit amet purus sit amet lacus sodales finibus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam eu odio eu lacus venenatis pretium. Nulla tincidunt lectus risus, vel dapibus sem elementum eu. Donec vel bibendum justo. Donec tristique ut ligula vel euismod. Aliquam ligula velit, consequat vel placerat id, blandit sit amet nibh.\s
                        Vivamus nec dui placerat, cursus magna eu, ultrices velit. Mauris elit tellus, commodo vitae venenatis ac, malesuada ac ex. Vivamus cursus diam in suscipit auctor. Integer faucibus vitae augue nec pulvinar. Praesent sit amet tempor orci. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras dolor tortor, auctor a porta non, sagittis eu leo. Donec neque dui, tincidunt a malesuada in, mollis nec eros. Nullam sit amet ipsum ut mi ullamcorper elementum semper quis eros. Aenean metus nunc, lobortis quis quam ac, lacinia lobortis nisi. Nullam porta ipsum ipsum, a mattis\s
                        1 Footnote 1
                        2 Second footnote, which contains multiple lines
                        Second line of Footnote #2 here.
                        3 This is the last one
                        Footer
                        Lines
                        3 total
                        Page 1/3""",
                """
                        HEADER Text
                        lorem dictum ac. Donec vel leo convallis, tincidunt dui sit amet, lobortis justo. Sed rhoncus vel odio ut vestibulum. Curabitur erat quam, vehicula eu nisl nec, dignissim consectetur libero. Etiam eget leo tortor.\s
                        No longer a Single-Line Page
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
