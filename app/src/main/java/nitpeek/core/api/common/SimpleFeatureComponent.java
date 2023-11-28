package nitpeek.core.api.common;

import java.util.Objects;
import java.util.Optional;

public final class SimpleFeatureComponent implements FeatureComponent {

    private final String description;
    private final TextSelection coordinates;
    private final String relevantTextPortion;

    public SimpleFeatureComponent(String description, TextSelection coordinates) {
        this(description, coordinates, null);
    }

    public SimpleFeatureComponent(String description, TextSelection coordinates, String relevantTextPortion) {
        this.description = description;
        this.coordinates = coordinates;
        this.relevantTextPortion = relevantTextPortion;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TextSelection getCoordinates() {
        return coordinates;
    }

    @Override
    public Optional<String> getRelevantTextPortion() {
        return Optional.ofNullable(relevantTextPortion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleFeatureComponent that = (SimpleFeatureComponent) o;
        return Objects.equals(description, that.description) && Objects.equals(coordinates, that.coordinates) && Objects.equals(relevantTextPortion, that.relevantTextPortion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, coordinates, relevantTextPortion);
    }
}
