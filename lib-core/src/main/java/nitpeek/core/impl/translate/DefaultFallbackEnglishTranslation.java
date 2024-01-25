package nitpeek.core.impl.translate;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

public final class DefaultFallbackEnglishTranslation implements Translation {

    private final Translation translation = new WrappingTranslation(new CoreEnglishTranslation(), new UntranslatableTranslation());

    @Override
    public String translate(String translationKey, Object... arguments) {
        return translation.translate(translationKey, arguments);
    }

    @Override
    public String translate(TextSelection textSelection) {
        return translation.translate(textSelection);
    }
}
