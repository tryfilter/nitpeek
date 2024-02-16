package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

interface ParagraphRenderer {
    String render(P paragraph);
    String renderFrom(int firstRun, P paragraph);
    String renderTo(int lastRun, P paragraph);
    String renderBetween(int firstRun, int lastRun, P paragraph);
}
