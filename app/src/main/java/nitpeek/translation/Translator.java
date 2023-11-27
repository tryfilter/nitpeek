package nitpeek.translation;

/**
 * This interface is likely to be expanded in the future and breaking compatibility with existing implementors is an
 * expected consequence.<br>
 * If you wish to avoid breakage of your implementation when this interface is expanded then subclass one of the default
 * translators in this package, e.g. DefaultEnglishTranslator. That way, any methods added to this interface that your
 * implementation is not yet supporting can produce sensible default translations.<br>
 * If extending a default translator is not an option for you, you may implement your custom translator in Kotlin and
 * use the delegation pattern.
 */
public interface Translator {

    String missingPagesComponentDescription(int firstMissingPage, int lastMissingPage);
    String missingPagesFeatureName();
    String missingPagesFeatureDescription();
}
