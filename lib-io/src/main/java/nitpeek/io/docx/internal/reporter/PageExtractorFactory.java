package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.report.ReportingException;
import nitpeek.io.docx.internal.pagesource.DocxPageExtractor;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.function.UnaryOperator;

public interface PageExtractorFactory {
    DocxPageExtractor createExtractor(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws ReportingException;
}