package nitpeek.core.api.analyze;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextPage;

import java.util.List;

/**
 * The core functionality interface. Instances of this type can be used to implement complex validation rules.
 * It is intentionally simple and declares few constraints on its contract. Concrete implementations may enforce
 * particular characteristics such as:<br>
 * <ul>
 *  <li>Thread safety
 *  <li>Repeat processing tolerance: an analyzer is tolerant of repeat processing if the features it produces are not
 *  affected by processing the same page more than once. Analyzers that break this assumption shall document this fact.
 *  <li>Order independence, which can be one of 3 degrees:
 * </ul>
 *  <ol>
 *      <li>
 *          Order Dependent: what features are produced by the analyzer depends on the order in which pages are processed.
 *          Generally, such analyzers should be fed pages in numerical ascending order of their page number unless specified
 *          otherwise.
 *      </li>
 *      <li>
 *          Weakly Order Independent: features are independent of page processing order as long as {@link #findFeatures()} is
 *          only called after all pages were processed. Otherwise, the analyzer may produce faulty features.
 *      </li>
 *      <li>
 *          Strongly Order Independent: features are independent of page processing order
 *      </li>
 *  </ol>
 */
public interface Analyzer {
    /**
     * Performs analysis to determine currently detectable features.<br>
     * Implementations shall provide the complete list of features they can detect with the currently available information.
     * That is, each invocation of this method should be treated by the implementation as potentially being the only invocation
     * in the object's lifetime.
     *
     * @return the list of features detected at the time of calling this method
     */
    List<Feature> findFeatures();

    /**
     * Ingests a blob of text, interpreted as the contents of one page, for analysis.
     *
     * @param page The text contents of one page
     */
    void processPage(TextPage page);
}
