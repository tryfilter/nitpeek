package nitpeek.translation;

/**
 * This class may be extended to create partially implemented translators or to create custom translators that are
 * resilient against new additions to the Translator interface: instead of a compilation/runtime error, yet not-implemented
 * methods will defer to this default english translation.
 */
public class DefaultEnglishTranslator implements Translator {

    @Override
    public String missingPagesComponentDescription(int firstMissingPage, int lastMissingPage) {
        return "Pages " + firstMissingPage + "-" + lastMissingPage + " have not been processed.";
    }
}
