package nitpeek.core.impl.common;

import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

import java.util.Optional;

/**
 * Convenience implementation.
 */
public final class AnonymousFeatureComponent implements FeatureComponent {

    private final String relevantTextPortion;
    private final TextSelection coordinates;
    private final String description;

    public AnonymousFeatureComponent(TextSelection coordinates) {
        this(null, coordinates);
    }

    public AnonymousFeatureComponent(String relevantTextPortion, TextSelection coordinates) {
        this(relevantTextPortion,
                coordinates,
                "This is an anonymous feature component, only intended for implementation details."
        );
    }

    public AnonymousFeatureComponent(String relevantTextPortion, TextSelection coordinates, String description) {
        this.relevantTextPortion = relevantTextPortion;
        this.coordinates = coordinates;
        this.description = description;
    }

    @Override
    public String getDescription(Translation translation) {
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
}
