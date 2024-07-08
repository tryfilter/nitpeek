package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.analyze.SimpleTextPage;
import com.nitpeek.core.impl.analyze.analyzer.CustomDescriptionAnalyzer;
import com.nitpeek.core.impl.analyze.analyzer.LiteralReplacer;
import com.nitpeek.core.impl.translate.CoreEnglishTranslation;
import com.nitpeek.core.impl.translate.IdentityTranslation;
import org.junit.jupiter.api.Test;
import com.nitpeek.test.core.testutil.FeatureAssert;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.REPLACE_LITERAL_COMPONENT_DESCRIPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class CustomDescriptionAnalyzerShould {

    private static final String FROM = "OLD";
    private static final String TO = "NEW";
    private static final TextPage PAGE = new SimpleTextPage("Some " + FROM + " news, from " + FROM + " sources", 0);

    private Analyzer getAnalyzer() {
        return new LiteralReplacer(FROM, TO);
    }

    @Test
    void preserveComponentsWithIdentityGenerator() {
        var originalAnalyzer = getAnalyzer();
        originalAnalyzer.processPage(PAGE);

        BiFunction<FeatureComponent, Translation, String> identityGenerator = FeatureComponent::getDescription;
        var customDescriptionAnalyzer = new CustomDescriptionAnalyzer(getAnalyzer(), identityGenerator);
        customDescriptionAnalyzer.processPage(PAGE);
        var featuresOriginalAnalyzer = originalAnalyzer.findFeatures();
        FeatureAssert.assertEqualFeatures(featuresOriginalAnalyzer, customDescriptionAnalyzer, new IdentityTranslation());
    }

    @Test
    void modifyComponentDescriptionsToConstant() {
        var newDescription = "Custom Description";
        var originalAnalyzer = getAnalyzer();
        var identityTranslation = new IdentityTranslation();
        originalAnalyzer.processPage(PAGE);

        var customDescriptionAnalyzer = new CustomDescriptionAnalyzer(getAnalyzer(), (component, i18n) -> newDescription);
        customDescriptionAnalyzer.processPage(PAGE);
        var actualDescriptions = getDescriptions(customDescriptionAnalyzer.findFeatures(), identityTranslation);
        assertEquals(List.of(newDescription, newDescription), actualDescriptions);
    }

    @Test
    void modifyComponentDescriptionsOriginalDependent() {
        var originalAnalyzer = getAnalyzer();
        var englishTranslation = new CoreEnglishTranslation();
        originalAnalyzer.processPage(PAGE);

        var customDescriptionAnalyzer = new CustomDescriptionAnalyzer(
                getAnalyzer(),
                (component, i18n) -> component.getDescription(englishTranslation) + component.getRelevantTextPortion().orElse("") + component.getCoordinates().fromInclusive().character()
        );
        customDescriptionAnalyzer.processPage(PAGE);
        var actualDescriptions = getDescriptions(customDescriptionAnalyzer.findFeatures(), englishTranslation);
        var expectedDescriptions = List.of(
                englishTranslation.translate(REPLACE_LITERAL_COMPONENT_DESCRIPTION.key(), TO) + FROM + 5, // index of first 'OLD'
                englishTranslation.translate(REPLACE_LITERAL_COMPONENT_DESCRIPTION.key(), TO) + FROM + 20 // index of second 'OLD'
        );
        assertEquals(expectedDescriptions, actualDescriptions);
    }

    private List<String> getDescriptions(List<Feature> features, Translation i18n) {
        return features.stream().map(Feature::getComponents).flatMap(Collection::stream).map(component -> component.getDescription(i18n)).toList();
    }
}