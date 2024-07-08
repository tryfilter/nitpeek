package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.types.CompositeRun;
import org.docx4j.wml.R;

import java.util.List;

public interface RunsCompositor {
    List<CompositeRun> composit(List<R> runs);
}