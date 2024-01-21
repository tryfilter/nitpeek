package nitpeek.translation;

import nitpeek.core.api.common.TextSelection;

public interface Translation {
    String translate(String translationKey, Object... arguments);
    String translate(TextSelection textSelection);

    static String untranslatable(String translationKey) {
        return "i18n N/A: '" + translationKey + "'";
    }
}
