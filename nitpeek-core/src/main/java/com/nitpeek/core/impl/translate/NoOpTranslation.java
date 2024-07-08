package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.translate.Translation;

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
