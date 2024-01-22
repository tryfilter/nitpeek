package nitpeek.core.impl.report;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.report.TextRangeDescription;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

import java.util.List;

import static nitpeek.core.impl.translate.helper.InternalTranslationKeys.*;

public final class FancyTextRangeDescription implements TextRangeDescription {

    private final Translation i18n;

    public FancyTextRangeDescription(Translation i18n) {
        this.i18n = i18n;
    }

    @Override
    public String describe(TextSelection textSelection) {
        TextCoordinate from = textSelection.fromInclusive();
        TextCoordinate to = textSelection.toInclusive();

        List<String> pageElements = describeRange(from.page(), to.page(), i18n.translate(PAGE.key()), false);
        List<String> lineElements = describeRange(from.line(), to.line(), i18n.translate(LINE.key()), !pageElements.get(1).isBlank());
        List<String> characterElements = describeRange(from.character(), to.character(), i18n.translate(CHARACTER.key()), !lineElements.get(1).isBlank());


        return pageElements.get(0) + prePad(lineElements.get(0)) + prePad(characterElements.get(0)) +
                prePad(pageElements.get(1)) + prePad(lineElements.get(1)) + prePad(characterElements.get(1));
    }

    private static String prePad(String value) {
        if (!value.isBlank() && !value.startsWith(",")) return " " + value;
        return value;
    }

    private List<String> describeRange(int fromInclusive, int toInclusive, String axisName, boolean parentIsRange) {
        if (parentIsRange) return List.of(i18n.translate(AXIS_COMPOUND.key(), axisName, fromInclusive), i18n.translate(AXIS_COMPOUND.key(), axisName, toInclusive));

        if (fromInclusive == toInclusive) {
            return List.of(i18n.translate(AXIS_PINPOINT.key(), axisName, fromInclusive), "");
        } else {
            String start = i18n.translate(AXIS_START.key(), axisName, fromInclusive);
            String end = i18n.translate(AXIS_END.key(), axisName, toInclusive);
            return List.of(start, end);
        }
    }
}
