package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.types.CompositeRun;
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
    private final ParagraphTransformer paragraphTransformer;

    FootnotesSegmentExtractor(WordprocessingMLPackage docx, int footnoteToExtract, ParagraphTransformer paragraphTransformer) {
        this.document = docx.getMainDocumentPart();
        this.footnoteToExtract = footnoteToExtract;
        this.paragraphTransformer = paragraphTransformer;
    }

    @Override
    public Optional<DocxSegment<CompositeRun>> extractSegment() {
        var footnotes = DocxUtil.getRelatedObject(document.getRelationshipsPart(), CTFootnotes.class);
        if (footnotes == null) return Optional.empty();

        var footnote = footnotes.getFootnote()
                .stream()
                .filter(f -> f.getId().intValue() == footnoteToExtract)
                .findFirst();

        List<P> originalParagraphs = DocxUtil.getNonEmptyParagraphs(footnote.map(CTFtnEdn::getContent).orElse(List.of()));
        var transformedParagraphs = originalParagraphs.stream().map(paragraphTransformer::transform).toList();

        return Optional.of(new SimpleDocxSegment<>(transformedParagraphs));
    }
}
