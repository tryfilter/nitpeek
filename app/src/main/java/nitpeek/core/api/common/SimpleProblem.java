package nitpeek.core.api.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record SimpleProblem(ProblemType type, List<ProblemComponent> components, double confidence) implements Problem {
    public SimpleProblem {
        if (confidence < 0 || confidence > 1)
            throw new IllegalArgumentException("confidence must be in the interval [0, 1] but was " + confidence);
        Objects.requireNonNull(type);
        Objects.requireNonNull(components);
        components = new ArrayList<>(components);
    }

    @Override
    public double getConfidence() {
        return confidence;
    }

    @Override
    public ProblemType getType() {
        return type;
    }

    @Override
    public List<ProblemComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
}
