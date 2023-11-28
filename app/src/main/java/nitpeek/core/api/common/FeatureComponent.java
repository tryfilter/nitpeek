package nitpeek.core.api.common;

import java.util.Optional;

/**
 * Most granular description of an issue in a text.<br>
 * It simply describes what is wrong, and where it is in the text.
 */
public interface FeatureComponent {

    String getDescription();

    TextSelection getCoordinates();

    Optional<String> getRelevantTextPortion();

}
