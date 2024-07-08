package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.internal.reporter.SegmentedDocxPage;

import java.util.List;

public interface DocxPageExtractor {
    List<SegmentedDocxPage<CompositeRun>> extractPages();
}
