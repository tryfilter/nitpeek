package nitpeek.core.api.common;

public record SimpleFeatureComponent(String description, TextSelection coordinates) implements FeatureComponent {
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TextSelection getCoordinates() {
        return coordinates;
    }
}
