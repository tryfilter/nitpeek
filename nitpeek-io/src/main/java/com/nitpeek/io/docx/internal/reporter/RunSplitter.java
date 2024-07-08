package com.nitpeek.io.docx.internal.reporter;

import org.docx4j.wml.R;

import java.util.Optional;

/**
 * Splits an existing run into two resulting runs.<br>
 * The original run may be modified, but a reference to it is always returned.
 */
interface RunSplitter {

    SplitResult splitAfter(R run, int lastIndexToKeep);

    /**
     * Represents the result of the operation of splitting a run object.<br>
     * Conceptually, the original run shall never be left "empty" as a result of the split.
     *
     * @param remaining a reference to the original run, modified as necessary to reflect the split
     * @param splitOff  a new run containing the other side of the split, if present
     */
    record SplitResult(R remaining, Optional<R> splitOff) {
    }
}