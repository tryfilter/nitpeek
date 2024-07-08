package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.translate.Translation;

public final class IdentityTranslation implements Translation {
    @Override
    public String translate(String translationKey, Object... arguments) {
        return translationKey;
    }

    @Override
    public String translate(TextSelection textSelection) {
        return textSelection.toString();
    }
}
