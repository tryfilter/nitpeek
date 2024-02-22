package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.wml.*;

import java.util.*;

import static nitpeek.util.collection.SafeSublist.subListFrom;
import static nitpeek.util.collection.SafeSublist.subListTo;


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
            default -> {
                // not implemented
            }
        }
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
