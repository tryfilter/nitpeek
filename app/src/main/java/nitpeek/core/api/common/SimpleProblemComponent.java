package nitpeek.core.api.common;

public record SimpleProblemComponent(String description, TextSelection coordinates) implements ProblemComponent {
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TextSelection getCoordinates() {
        return coordinates;
    }
}
