package nitpeek.core.impl.translate;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ResourceBundleTranslation implements Translation {

    private final ResourceBundle resourceBundle;
    private final Locale locale;

    public ResourceBundleTranslation(ResourceBundle resourceBundle, Locale locale) {
        this.resourceBundle = resourceBundle;
        this.locale = locale;
    }

    @Override
    public String translate(String translationKey, Object... arguments) {
        try {
            String pattern = resourceBundle.getString(translationKey);
            if (arguments.length == 0) return pattern;

            var messageFormat = new MessageFormat(pattern, locale);
            messageFormat.applyPattern(pattern);
            return messageFormat.format(arguments, new StringBuffer(), null).toString();
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * Override this method if you require specialized formatting of text selections
     *
     * @param textSelection the text selection to translate
     * @return null, allowing a wrapping translation to provide a default translation
     */
    @Override
    public String translate(TextSelection textSelection) {
        return null;
    }
}
