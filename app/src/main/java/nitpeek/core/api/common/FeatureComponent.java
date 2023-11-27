package nitpeek.core.api.common;

/**
 * Most granular description of an issue in a text.<br>
 * It simply describes what is wrong, and where it is in the text.
 */
public interface FeatureComponent {

    String getDescription();

    TextSelection getCoordinates();

}
