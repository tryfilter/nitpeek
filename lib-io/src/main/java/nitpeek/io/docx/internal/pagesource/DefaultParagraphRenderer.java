package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.wml.*;

import java.util.*;

import static nitpeek.util.collection.SafeSublist.subListFrom;
import static nitpeek.util.collection.SafeSublist.subListTo;

/**
 * This is a simplified implementation:
 * It assumes standard arabic numbering (1, 2, 3, ...) for both the current page and footnote references.
 * Additionally, it assumes that the id of the footnote corresponds to the actual ordinal value of the footnote
 * (e.g. footnote with id=2 is the second footnote) even though this is not guaranteed by the spec.
 *
 * It is not section-aware, i.e. it treats all footnotes as belonging to the same section (the entire document).
 */

public final class DefaultParagraphRenderer implements ParagraphRenderer {
    private final int currentPage;
    private final int pageCount;

    private boolean inComplexField = false;
    private StringBuilder result = new StringBuilder();
    private static final Set<STFldCharType> PROCESSING_CHANGE = EnumSet.of(STFldCharType.BEGIN, STFldCharType.END);
    private static final String FIELD_CODE_NAME = "instrText";
    private static final String TEXT_CODE_NAME = "t";

    public DefaultParagraphRenderer(int currentPage, int pageCount) {
        this.currentPage = currentPage;
        this.pageCount = pageCount;
    }

    @Override
    public String render(P paragraph) {
        return renderRuns(DocxUtil.getRuns(paragraph));
    }

    @Override
    public String renderFrom(int firstRun, P paragraph) {
        var relevantRuns = subListFrom(DocxUtil.getRuns(paragraph), firstRun);
        return renderRuns(relevantRuns);
    }

    @Override
    public String renderTo(int lastRun, P paragraph) {
        var relevantRuns = subListTo(DocxUtil.getRuns(paragraph), lastRun);
        return renderRuns(relevantRuns);
    }

    @Override
    public String renderBetween(int firstRun, int lastRun, P paragraph) {
        var allRuns = DocxUtil.getRuns(paragraph);
        return renderRuns(allRuns.subList(firstRun, lastRun + 1));
    }

    private String renderRuns(List<R> runs) {
        inComplexField = false;
        result = new StringBuilder();

        for (var run : runs) {
            applyContents(getContent(run));
        }
        return result.toString();
    }

    private void applyContents(List<JAXBElement<?>> contents) {
        for (var element : contents) {
            applyElement(element);
        }
    }

    private void applyElement(JAXBElement<?> element) {
        switch (element.getValue()) {
            case Text text -> applyText(text, element);
            case FldChar fldChar when isProcessingSwitch(fldChar) -> inComplexField = !inComplexField;
            case CTFtnEdnRef footnote -> result.append(footnote.getId().intValue());
            case R.FootnoteRef footnoteRef -> result.append(getFootnoteReference(footnoteRef));
            default -> {
                // not implemented
            }
        }
    }

    private String getFootnoteReference(R.FootnoteRef reference) {
        if (reference.getParent() instanceof R run
                && run.getParent() instanceof P paragraph
                && paragraph.getParent() instanceof CTFtnEdn footnote) {
            return String.valueOf(footnote.getId().intValue());
        }
        return "";
    }

    private boolean isProcessingSwitch(FldChar fldChar) {
        return PROCESSING_CHANGE.contains(fldChar.getFldCharType());
    }

    private void applyText(Text text, JAXBElement<?> parent) {
        var elementName = parent.getName().getLocalPart();
        switch (elementName) {
            case FIELD_CODE_NAME -> {
                if (text.getValue().contains("NUMPAGES")) result.append(pageCount);
                else if (text.getValue().contains("PAGE")) result.append(currentPage);
            }
            case TEXT_CODE_NAME -> {
                // For some reason an instance of the last rendered value is saved in the docx, even though it is not
                // generally applicable. We skip this 'dummy' value.
                if (!inComplexField) result.append(text.getValue());
            }
        }
    }

    private List<JAXBElement<?>> getContent(R run) {
        return DocxUtil.keepJaxbElements(run.getContent());
    }
}
