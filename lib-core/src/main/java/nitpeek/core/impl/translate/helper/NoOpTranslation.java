package nitpeek.core.impl.translate.helper;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

public final class NoOpTranslation implements Translation {
    @Override
    public String translate(String translationKey, Object... arguments) {
        return translationKey;
    }

    @Override
    public String translate(TextSelection textSelection) {
        return textSelection.toString();
    }
}
