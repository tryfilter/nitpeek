package nitpeek.core.api.report;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.translation.Translator;

import java.util.List;

public final class FancyTextRangeDescription implements TextRangeDescription {

    private final Translator i18n;

    public FancyTextRangeDescription(Translator translator) {
        this.i18n = translator;
    }

    @Override
    public String describe(TextSelection textSelection) {
        TextCoordinate from = textSelection.fromInclusive();
        TextCoordinate to = textSelection.toInclusive();

        List<String> pageElements = describeRange(from.page(), to.page(), i18n.page(), false);
        List<String> lineElements = describeRange(from.line(), to.line(), i18n.line(), !pageElements.get(1).isBlank());
        List<String> characterElements = describeRange(from.character(), to.character(), i18n.character(), !lineElements.get(1).isBlank());


        return pageElements.get(0) + prePad(lineElements.get(0)) + prePad(characterElements.get(0)) +
                prePad(pageElements.get(1)) + prePad(lineElements.get(1)) + prePad(characterElements.get(1));
    }

    private static String prePad(String value) {
        if (!value.isBlank() && !value.startsWith(",")) return " " + value;
        return value;
    }

    private List<String> describeRange(int fromInclusive, int toInclusive, String axisName, boolean parentIsRange) {
        if (parentIsRange) return List.of(i18n.axisCompound(axisName, fromInclusive), i18n.axisCompound(axisName, toInclusive));

        if (fromInclusive == toInclusive) {
            return List.of(i18n.axisPinpoint(axisName, fromInclusive), "");
        } else {
            String start = i18n.axisStart(axisName, fromInclusive);
            String end = i18n.axisEnd(axisName, toInclusive);
            return List.of(start, end);
        }
    }
}
