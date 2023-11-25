package nitpeek.core.api.analyze;

import nitpeek.core.api.common.Problem;

import java.util.List;


public interface Analyzer {
    /**
     * Performs analysis to determine currently detectable problems
     *
     * @return the list of found problems at the time of calling this method
     */
    List<Problem> findProblems();

    /**
     * Ingests a blob of text, interpreted as the contents of one page, for analysis
     *
     * @param page The text contents of one page
     */
    void processPage(TextPage page);
}
