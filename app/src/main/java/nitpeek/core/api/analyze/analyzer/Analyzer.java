package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.Feature;

import java.util.List;

/**
 * The core functionality interface. Instances of this type can be used to implement complex validation rules.
 * It is intentionally simple and declares few constraints on its contract. Concrete implementations may enforce
 * particular characteristics e.g. thread safety, state freezing etc.
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
