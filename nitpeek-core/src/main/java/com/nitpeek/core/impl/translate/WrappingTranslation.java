package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.translate.Translation;

public final class WrappingTranslation implements Translation {

    private final Translation mainTranslation;
    private final Translation fallbackTranslation;

    public WrappingTranslation(Translation mainTranslation, Translation fallbackTranslation) {
        this.mainTranslation = mainTranslation;
        this.fallbackTranslation = fallbackTranslation;
    }

    @Override
    public String translate(String translationKey, Object... arguments) {
        var translated = mainTranslation.translate(translationKey, arguments);
        if (translated != null) return translated;

        return fallbackTranslation.translate(translationKey, arguments);
    }

    @Override
    public String translate(TextSelection textSelection) {
        var translated = mainTranslation.translate(textSelection);
        if (translated != null) return translated;

        return fallbackTranslation.translate(textSelection);
    }
}
