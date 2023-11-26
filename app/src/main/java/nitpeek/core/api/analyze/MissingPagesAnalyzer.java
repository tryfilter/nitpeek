package nitpeek.core.api.analyze;

import nitpeek.core.api.common.*;
import nitpeek.core.api.common.util.PageRange;

import java.util.*;

public class MissingPagesAnalyzer implements Analyzer {

    private final Set<Integer> processedPageNumbers = new HashSet<>();

    @Override
    public void processPage(TextPage page) {
        processedPageNumbers.add(page.getPageNumber());
    }

    @Override
    public List<Problem> findProblems() {
        if (processedPageNumbers.isEmpty())
            return List.of(new SimpleProblem(StandardProblem.MISSING_PAGES.getType(), Collections.emptyList(), Confidence.HIGH.value()));

        var sortedPageNumbers = new TreeSet<>(processedPageNumbers);
        List<Problem> problems = new ArrayList<>();

        if (sortedPageNumbers.first() != 0) {
            ProblemComponent missingPageRange = component(0, sortedPageNumbers.first() - 1);
            problems.add(new SimpleProblem(StandardProblem.MISSING_PAGES.getType(), Collections.singletonList(missingPageRange), Confidence.MEDIUM.value()));
        }

        List<PageRange> missingSections = getMissingSections(sortedPageNumbers);
        for (var missingSection : missingSections) {
            ProblemComponent missingPageRange = component(missingSection.firstPage(), missingSection.lastPage());
            problems.add(new SimpleProblem(StandardProblem.MISSING_PAGES.getType(), Collections.singletonList(missingPageRange), Confidence.HIGH.value()));
        }

        return problems;
    }

    private ProblemComponent component(int firstMissingPage, int lastMissingPage) {
        TextCoordinate firstMissing = new TextCoordinate(firstMissingPage, 0, 0);
        TextCoordinate lastMissing = new TextCoordinate(lastMissingPage, Integer.MAX_VALUE, Integer.MAX_VALUE);
        TextSelection missingSelection = new TextSelection(firstMissing, lastMissing);

        return new SimpleProblemComponent("Pages " + firstMissingPage + "-" + lastMissingPage + " are missing.", missingSelection);
    }

    private List<PageRange> getMissingSections(NavigableSet<Integer> sortedPageNumbers) {
        List<PageRange> missingSections = new ArrayList<>();
        Integer previousPageNumber = null;
        for (Integer pageNumber : sortedPageNumbers) {
            if (previousPageNumber != null && (pageNumber - previousPageNumber) > 1) {
                missingSections.add(new PageRange(previousPageNumber + 1, pageNumber - 1));
            }
            previousPageNumber = pageNumber;
        }
        return missingSections;
    }
}
