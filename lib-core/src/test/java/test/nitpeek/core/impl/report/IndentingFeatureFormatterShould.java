package test.nitpeek.core.impl.report;

import nitpeek.core.api.common.*;
import nitpeek.core.api.report.FeatureComponentFormatter;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.SimpleFeatureType;
import nitpeek.core.impl.report.Indent;
import nitpeek.core.impl.report.IndentingFeatureFormatter;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.core.api.translate.Translation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nitpeek.core.impl.translate.CoreTranslationKeys.FOUND_FEATURE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class IndentingFeatureFormatterShould {

    private static final Indent indent = new Indent(1, "+");

    private static final TextSelection DUMMY_SELECTION = new TextSelection(new TextCoordinate(0, 0, 0), new TextCoordinate(2, 2, 2));
    private static final List<FeatureComponent> COMPONENTS = List.of(
            new SimpleFeatureComponent("COMPONENT1", DUMMY_SELECTION),
            new SimpleFeatureComponent("COMPONENT2", DUMMY_SELECTION),
            new SimpleFeatureComponent("COMPONENT3", DUMMY_SELECTION)
    );

    private final IndentingFeatureFormatter formatter = new IndentingFeatureFormatter(new DummyTranslator(), indent, new DummyComponentFormatter());

    @Test
    void indentDescriptionAndComponentsFromNameAndSeparateComponentsByEmptyLines() {
        String expected = """
                Feature: FEATURE_NAME
                +Description: DESCRIPTION
                
                +COMPONENT1
                                
                +COMPONENT2
                                
                +COMPONENT3""";
        String actual = formatter.format(new DummyFeature());

        assertEquals(expected, actual);
    }

    @Test
    void produceOnlyNameAndDescriptionWhenNoComponentsPresent() {
        String expected = """
                Feature: FEATURE_NAME
                +Description: DESCRIPTION""";
        String actual = formatter.format(new DummyFeature() {
            @Override
            public List<FeatureComponent> getComponents() {
                return List.of();
            }
        });

        assertEquals(expected, actual);
    }

    private static class DummyComponentFormatter implements FeatureComponentFormatter {
        private static final Translation defaultTranslation = new DefaultFallbackEnglishTranslation();

        @Override
        public String format(FeatureComponent featureComponent) {
            return featureComponent.getDescription(defaultTranslation);
        }
    }

    private static class DummyFeature implements Feature {

        @Override
        public double getConfidence() {
            return 0;
        }

        @Override
        public FeatureType getType() {
            return new DummyFeatureType("0", "FEATURE_NAME", SimpleFeatureType.Classification.INFO, "DESCRIPTION");
        }

        @Override
        public List<FeatureComponent> getComponents() {
            return COMPONENTS;
        }
    }

    private static class DummyTranslator implements Translation {

        private static final Translation englishTranslation = new DefaultFallbackEnglishTranslation();

        @Override
        public String translate(String translationKey, Object... arguments) {
            if (FOUND_FEATURE_NAME.key().equals(translationKey)) return "Feature: " + arguments[0];
            return englishTranslation.translate(translationKey, arguments);
        }

        @Override
        public String translate(TextSelection textSelection) {
            return englishTranslation.translate(textSelection);
        }
    }
}
