package nitpeek.io.docx.internal.pagesource;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.P;

import java.util.List;
import java.util.Optional;

final class FootnotesSegmentExtractor implements DocxSegmentExtractor {

    private final MainDocumentPart document;
    private final int footnoteToExtract;

    FootnotesSegmentExtractor(WordprocessingMLPackage docx, int footnoteToExtract) {
        this.document = docx.getMainDocumentPart();
        this.footnoteToExtract = footnoteToExtract;
    }

    @Override
    public Optional<DocxSegment> extractSegment() {
        var footnotes = DocxUtil.getRelatedObject(document.getRelationshipsPart(), CTFootnotes.class);
        if (footnotes == null) return Optional.empty();

        var footnote = footnotes.getFootnote()
                .stream()
                .filter(f -> f.getId().intValue() == footnoteToExtract)
                .findFirst();

        var paragraphs = DocxUtil.keepElementsOfType(footnote.map(CTFtnEdn::getContent).orElse(List.of()), P.class);

        return Optional.of(new DocxSegment(paragraphs));
    }
}
