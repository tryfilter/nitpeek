package nitpeek.core.api.config.standard.footnote;

import nitpeek.core.api.common.TextSelection;

import java.util.List;

/**
 * @param content   the lines composing the text content of the footnote, including the footnote ordinal if applicable
 * @param selection the {@code TextSelection} that covers the characters representing the reference, in the
 *                  coordinate system defined by only the body portions of the pages
 */
public record FootnoteContent(List<String> content, TextSelection selection) {
}