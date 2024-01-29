package nitpeek.core.impl.translate;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

public final class NoOpTranslation implements Translation {
    @Override
    public String translate(String translationKey, Object... arguments) {
        return null;
    }

    @Override
    public String translate(TextSelection textSelection) {
        return null;
    }
}
