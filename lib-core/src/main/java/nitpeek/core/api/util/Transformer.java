package nitpeek.core.api.util;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextPage;

/**
 * A Transformer is a bridge between an analyzer and a source of pages, whether that source is another analyzer, an
 * actual page source, or some other component entirely.<br>
 * Its purpose is to transparently translate pages in some input form into pages appropriate for the analyzer and to
 * translate the features produced by said analyzer into features that correspond with the form of the original pages.
 */
public interface Transformer {
    /**
     *
     * @param original a text page coming from the upstream source
     * @return a new text page, transformed into a format suitable for the downstream page consumer
     */
    TextPage transformPage(TextPage original);

    /**
     *
     * @param original a feature coming from the downstream page consumer
     * @return a new feature, transformed into a format in line with the page originally processed by this transformer using {@link #transformPage(TextPage)}
     */
    Feature transformFeature(Feature original);
}
