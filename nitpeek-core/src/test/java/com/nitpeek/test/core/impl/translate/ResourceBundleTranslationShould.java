package com.nitpeek.test.core.impl.translate;

import com.nitpeek.core.api.translate.LocaleProvider;
import com.nitpeek.core.api.translate.TranslationProvider;
import com.nitpeek.core.impl.translate.CurrentDefaultLocaleProvider;
import com.nitpeek.core.impl.translate.ResourceBundleTranslationProvider;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ResourceBundleTranslationShould {

    private static final LocaleProvider defaultLocale = new CurrentDefaultLocaleProvider();

    private static final String keySimple = "test.translation.key.simple";
    private static final String keyMissingInGerman = "test.translation.key.missing";
    private static final String keyParameterized = "test.translation.key.parameterized";

    private static final TranslationProvider translationProvider = new ResourceBundleTranslationProvider() {
        @Override
        protected String getBaseResourcePath() {
            return "com.nitpeek.core.impl.translate.TestBundle";
        }
    };


    @Test
    void fallbackToDefaultTranslationOnMissingLocale() {
        Locale.setDefault(Locale.CHINESE);
        var bundleTranslation = translationProvider.getTranslation(defaultLocale);

        String actual = bundleTranslation.translate(keySimple);
        String expected = "This is a simple english sentence.";

        assertEquals(expected, actual);
    }

    @Test
    void fallbackToDefaultTranslationOnMissingKey() {
        Locale.setDefault(Locale.GERMAN);
        var bundleTranslation = translationProvider.getTranslation(defaultLocale);

        String actual = bundleTranslation.translate(keyMissingInGerman);
        String expected = "This value is only translated in the default resource bundle.";

        assertEquals(expected, actual);
    }

    @Test
    void pickCorrectResource() {
        Locale.setDefault(Locale.GERMAN);
        var bundleTranslation = translationProvider.getTranslation(defaultLocale);

        String actual = bundleTranslation.translate(keySimple);
        String expected = "Dies ist ein einfacher englischer Satz.";

        assertEquals(expected, actual);
    }

    @Test
    void applyParametersInFormatPatterns() {
        Locale.setDefault(Locale.ENGLISH);
        var bundleTranslation = translationProvider.getTranslation(defaultLocale);
        var kittenCount = 7;
        var location = "Salem";
        double weight = 300.633891;

        String actual = bundleTranslation.translate(keyParameterized, kittenCount, location, weight);
        String expected = "There were " + kittenCount + " kitten in " + location + ", weighing " + new DecimalFormat("0.00").format(weight) + "g each.";

        assertEquals(expected, actual);
    }

}