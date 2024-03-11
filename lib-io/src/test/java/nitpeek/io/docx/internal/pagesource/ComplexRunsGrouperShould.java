package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBException;
import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.pagesource.run.ComplexRun;
import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.XmlUtils;
import org.docx4j.wml.P;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class ComplexRunsGrouperShould {

    @Test
    void groupComplexRunsIntoSingleCompositeRun() throws JAXBException {
        int currentPageIndex = 3; // 0-based
        int pageCount = 5;
        P paragraph = createParagraphWithComplexRuns();
        var complexRunsGrouper = new ComplexRunsGrouper();
        List<CompositeRun> groupedRuns = complexRunsGrouper.composit(DocxUtil.getRuns(paragraph));
        assertEquals(4, groupedRuns.size());
        var runRenderer = new SimpleRunRenderer(currentPageIndex, pageCount, new SimpleArabicNumberRenderer());

        CompositeRun firstRun = groupedRuns.get(0);
        assertFalse(firstRun instanceof ComplexRun); // simple run
        assertEquals(1, firstRun.componentRuns().size());
        assertEquals("Page ", runRenderer.render(firstRun));

        CompositeRun secondRun = groupedRuns.get(1);
        assertInstanceOf(ComplexRun.class, secondRun); // complex run
        assertEquals(5, secondRun.componentRuns().size());
        assertEquals(currentPageIndex + 1, Integer.parseInt(runRenderer.render(secondRun)));

        CompositeRun thirdRun = groupedRuns.get(2);
        assertFalse(thirdRun instanceof ComplexRun); // simple run
        assertEquals(1, thirdRun.componentRuns().size());
        assertEquals("/", runRenderer.render(thirdRun));

        CompositeRun fourthRun = groupedRuns.get(3);
        assertInstanceOf(ComplexRun.class, fourthRun); // complex run
        assertEquals(5, fourthRun.componentRuns().size());
        assertEquals(pageCount, Integer.parseInt(runRenderer.render(fourthRun)));
    }

    private P createParagraphWithComplexRuns() throws JAXBException {
        return (P) XmlUtils.unmarshalString("""
                                <w:p xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                                        <w:pPr>
                                            <w:pStyle w:val="Footer" />
                                            <w:jc w:val="center" />
                                        </w:pPr>
                                        <w:r>
                                            <w:t xml:space="preserve">Page </w:t>
                                        </w:r>
                <!-- first complex run begins here -->
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
                <!-- first complex run ends here -->
                                        <w:r>
                                            <w:t>/</w:t>
                                        </w:r>
                <!-- second complex run begins here -->
                                        <w:r>
                                            <w:fldChar w:fldCharType="begin" />
                                        </w:r>
                                        <w:r>
                                            <w:instrText xml:space="preserve"> NUMPAGES </w:instrText>
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
                <!-- second complex run ends here -->
                                    </w:p>""");
    }
}