package nitpeek.core.api.common;

import nitpeek.core.api.translate.Translation;

import java.util.Optional;

/**
 * Most granular description of a feature in a text.<br>
 * It simply describes a feature or part thereof, and where precisely it is located in the text.
 */
public interface FeatureComponent {

    String getDescription(Translation translation);

    TextSelection getCoordinates();

    Optional<String> getRelevantTextPortion();

}
