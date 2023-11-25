package nitpeek.core.api.report;

import nitpeek.core.api.common.Problem;

import java.util.List;

public interface Reporter {

    void reportProblems(List<Problem> problems);
}
