package com.nitpeek.core.impl.common;

import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.translate.IdentityTranslation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class SimpleFeatureComponent implements FeatureComponent {

    private final Function<Translation, String> descriptionProvider;
    private final TextSelection coordinates;
    private final String relevantTextPortion;

    private static final Translation identity = new IdentityTranslation();

    /**
     * This constructor bypasses internationalization: use with care.
     */
    public SimpleFeatureComponent(String description, TextSelection coordinates) {
        this(description, coordinates, null);
    }

    /**
     * This constructor bypasses internationalization: use with care.
     */
    public SimpleFeatureComponent(String description, TextSelection coordinates, String relevantTextPortion) {
        this.descriptionProvider = translation -> description;
        this.coordinates = coordinates;
        this.relevantTextPortion = relevantTextPortion;
    }

    public SimpleFeatureComponent(Function<Translation, String> descriptionProvider, TextSelection coordinates) {
        this(descriptionProvider, coordinates, null);
    }

    public SimpleFeatureComponent(Function<Translation, String> descriptionProvider, TextSelection coordinates, String relevantTextPortion) {
        this.descriptionProvider = descriptionProvider;
        this.coordinates = coordinates;
        this.relevantTextPortion = relevantTextPortion;
    }

    @Override
    public String getDescription(Translation translation) {
        return descriptionProvider.apply(translation);
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
        return Objects.equals(descriptionProvider.apply(identity), that.descriptionProvider.apply(identity)) &&
                Objects.equals(coordinates, that.coordinates) &&
                Objects.equals(relevantTextPortion, that.relevantTextPortion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptionProvider.apply(identity), coordinates, relevantTextPortion);
    }
}
