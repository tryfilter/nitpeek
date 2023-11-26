package nitpeek.core.api.common;

public enum StandardProblem {

    MISSING_PAGES(new ProblemType("Missing Pages", ProblemType.Severity.LOW, "Fewer pages were processed than expected."));
    private final ProblemType type;

    StandardProblem(ProblemType type) {
        this.type = type;
    }

    public ProblemType getType() {
        return type;
    }
}
