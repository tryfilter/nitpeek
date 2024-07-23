package com.nitpeek.core.api.process;

import com.nitpeek.core.api.report.ReportingException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * Unified API for applying the full nitpeek process to a single input document.<br>
 * Note that the semantics of {@code input} and {@code analysisResult} are specific to each concrete implementation.<br>
 * Note also that concrete implementations may choose to selectively activate only a subset of the {@code Rule}s provided
 * by {@code ruleSetsToApply} for specific contexts of the input document (e.g. apply certain rules only to footnotes).
 */
public interface Nitpicker {

    /**
     * @param input           the stream providing the input document; specific format depends on implementors
     * @param analysisResult  the stream to which the found features will be reported; specific format depends on implementors
     * @param ruleSetsToApply the collection of {@code RuleSet}s to use for finding features in the input; implementors may
     *                        choose to activate different constellations of {@code RuleSet}s for different contexts of
     *                        the operation (e.g. apply certain rules only to footnotes)
     * @throws ReportingException when there is an issue in the nitpicking chain; implementor-specific
     */
    void nitpick(InputStream input, OutputStream analysisResult, Set<RuleSetProvider> ruleSetsToApply) throws ReportingException;
}