package nitpeek.core.api.translate;

import nitpeek.core.api.common.TextSelection;

/**
 * General interface for providing a Translation.
 * Implementations are expected to return null when unable to translate (e.g. because they don't recognize the
 * translation key, or are not equipped to handle {@code TextSelection}s). <br>
 * This way, specialized {@code Translation}s can be wrapped in more general {@code Translation}s which in turn can
 * provide fallback values for missing capabilities in the specialized {@code Translation}s.
 */
public interface Translation {
    String translate(String translationKey, Object... arguments);
    String translate(TextSelection textSelection);
}
