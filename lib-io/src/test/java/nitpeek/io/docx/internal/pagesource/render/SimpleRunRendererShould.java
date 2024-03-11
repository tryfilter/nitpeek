package nitpeek.io.docx.internal.pagesource.render;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.pagesource.run.ComplexRun;
import nitpeek.io.docx.internal.pagesource.run.SingletonRun;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.RunRenderer;
import nitpeek.io.docx.testutil.DocxTestUtil;
import org.docx4j.XmlUtils;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SimpleRunRendererShould {

    private static final int pageIndex = 3;
    private static final int humanPageNumber = pageIndex + 1;
    private static final int pageCount = 20;

    private final RunRenderer runRenderer = new SimpleRunRenderer(pageIndex, pageCount, new NumberRenderer() {
        @Override
        public String renderFootnoteNumber(int footnoteNumber) {
            return renderFootnote(footnoteNumber);
        }

        @Override
        public String renderPageNumber(int pageNumber) {
            return renderPageNo(pageNumber);
        }
    });

    @Test
    void renderSimpleRun() {
        String text = "Veni Vidi Vici";
        R simpleRun = DocxTestUtil.createSampleRun(text);
        CompositeRun wrappedRun = new SingletonRun(simpleRun);

        assertEquals(text, runRenderer.render(wrappedRun));
    }

    @Test
    void renderComplexField() throws JAXBException {
        CompositeRun complexFieldRun = createComplexFieldCurrentPage();

        assertEquals(renderPageNo(humanPageNumber), runRenderer.render(complexFieldRun));
    }

    private CompositeRun createComplexFieldCurrentPage() throws JAXBException {
        P paragraphContainingComplexField = (P) XmlUtils.unmarshalString("""
                <w:p xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                        <w:r>
                            <w:fldChar w:fldCharType="begin" />
                        </w:r>
                        <w:r>
                            <w:instrText xml:space="preserve"> PAGE </w:instrText>
                        </w:r>
                        <w:r>
                            <w:fldChar w:fldCharType="separate" />
                        </w:r>
                        <w:r>
                            <w:t>3</w:t>
                        </w:r>
                        <w:r>
                            <w:fldChar w:fldCharType="end" />
                        </w:r>
                    </w:p>""");
        return new ComplexRun(DocxUtil.getRuns(paragraphContainingComplexField));
    }

    @Test
    void renderFootnoteReference() throws JAXBException {
        CompositeRun footnoteRef = createFootnoteReference();

        assertEquals(renderFootnote(42), runRenderer.render(footnoteRef));
    }

    private CompositeRun createFootnoteReference() throws JAXBException {
        R footnoteReference = (R) XmlUtils.unmarshalString("""
                <w:r xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:rPr>
                        <w:rStyle w:val="FootnoteReference" />
                    </w:rPr>
                    <w:footnoteReference w:id="42" />
                </w:r>""");
        return new SingletonRun(footnoteReference);
    }

    @Test
    void renderFootnoteNumber() throws JAXBException {
        CompositeRun footnoteRef = createFootnoteRun();

        assertEquals(renderFootnote(49), runRenderer.render(footnoteRef));
    }

    private CompositeRun createFootnoteRun() throws JAXBException {
        CTFootnotes footnotes = (CTFootnotes) ((JAXBElement<?>)XmlUtils.unmarshalString("""
                <w:footnotes xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                    <w:footnote w:type="separator" w:id="-1">
                        <w:p>
                            <w:r>
                                <w:rPr>
                                    <w:color w:val="000000" />
                                </w:rPr>
                                <w:separator />
                            </w:r>
                        </w:p>
                    </w:footnote>
                    <w:footnote w:type="continuationSeparator" w:id="0">
                        <w:p>
                            <w:r>
                                <w:continuationSeparator />
                            </w:r>
                        </w:p>
                    </w:footnote>
                    <w:footnote w:id="49">
                        <w:p>
                            <w:pPr>
                                <w:pStyle w:val="Footnote" />
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rStyle w:val="FootnoteReference" />
                                </w:rPr>
                                <w:footnoteRef />
                            </w:r>
                            <w:r>
                                <w:t>Footnote text, not relevant</w:t>
                            </w:r>
                        </w:p>
                    </w:footnote>
                </w:footnotes>""")).getValue();
        CTFtnEdn footnote = footnotes.getFootnote().get(2); // the first 2 elements are not "real" footnotes: they represent the horizontal line separator before the footnotes section
        P paragraph = DocxUtil.getAllParagraphs(footnote.getContent()).getFirst();
        R footnoteRun = DocxUtil.getRuns(paragraph).getFirst();
        return new SingletonRun(footnoteRun);
    }


    private String renderFootnote(int footnote) {
        return "FOOTNOTE#" + footnote;
    }

    private String renderPageNo(int pageIndex) {
        return "//" + pageIndex;
    }
}