package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.translate.Translation;

public final class UntranslatableTranslation implements Translation {
    @Override
    public String translate(String translationKey, Object... arguments) {
        return untranslatable(translationKey);
    }

    @Override
    public String translate(TextSelection textSelection) {
        return untranslatable(textSelection.toString());
    }

    private static String untranslatable(String translationKey) {
        return "i18n N/A: '" + translationKey + "'";
    }
}
