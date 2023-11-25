package nitpeek.core.api.common;

import java.util.List;

public interface Problem {

    /**
     * @return a value between 0 and 1 inclusive representing how confident the program is that
     * this instance of Problem is not a false positive.
     */
    double getConfidence();

    ProblemType getType();

    List<ProblemComponent> getComponents();
}
