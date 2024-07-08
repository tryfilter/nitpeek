package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.pagesource.DocxPageExtractor;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.function.UnaryOperator;

public interface PageExtractorFactory {
    DocxPageExtractor createExtractor(WordprocessingMLPackage docx, UnaryOperator<DocxPage<CompositeRun>> pageTransformer) throws ReportingException;
}