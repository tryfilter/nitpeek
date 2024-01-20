package nitpeek.translation;

import nitpeek.core.api.common.TextSelection;

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
