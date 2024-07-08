package com.nitpeek.client;

import com.nitpeek.client.application.Application;
import com.nitpeek.client.plugin.PluginManager;
import com.nitpeek.client.plugin.ServiceProviderPluginManager;
import com.nitpeek.client.ruleset.Language;
import com.nitpeek.client.ruleset.LanguageRuleSetFilter;
import com.nitpeek.client.translation.AllPluginsTranslationProviderFactory;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.LocaleProvider;
import com.nitpeek.core.api.translate.TranslationProvider;
import com.nitpeek.io.docx.util.EasyDocxAnnotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DocxAnnotatingApplication implements Application {

    private final PluginManager pluginManager = new ServiceProviderPluginManager();
    private final LocaleProvider localeProvider;
    private final Path inputFolder;
    private final Path outputFolder;
    private final Logger log = LoggerFactory.getLogger(DocxAnnotatingApplication.class);

    private final Language language;

    private static final PathMatcher DOCX_EXTENSION = FileSystems.getDefault().getPathMatcher("glob:*.docx");

    private final TranslationProvider translationProvider = new AllPluginsTranslationProviderFactory(pluginManager).createTranslationProvider();

    public DocxAnnotatingApplication(LocaleProvider localeProvider, Path inputFolder, Path outputFolder, Language language) {
        this.language = language;
        if (!inputFolder.toFile().isDirectory()) throw new IllegalArgumentException("input directory not found: " + inputFolder);
        if (!outputFolder.toFile().isDirectory()) throw new IllegalArgumentException("output directory not found: " + inputFolder);

        this.localeProvider = localeProvider;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    @Override
    public void run() throws ReportingException {
        var allRuleSetsFromPlugins = new ServiceProviderPluginManager().getRuleSetProviders();

        var i18n = translationProvider.getTranslation(localeProvider);
        var keepRuleSetsApplicableForSelectedLanguage = new LanguageRuleSetFilter(language);
        var annotator = new EasyDocxAnnotator(keepRuleSetsApplicableForSelectedLanguage.filter(allRuleSetsFromPlugins), i18n);

        printMessage("Processing DOCX files...");
        var files = getDocxFilesInInputFolder();
        for (var inputDocx : files) {
            printMessage("Processing file " + inputDocx);
            annotator.annotateFeatures(inputDocx, outputFolder);
        }
        printMessage("Finished processing " + files.size() + " files.");
    }

    private void printMessage(String message) {
        log.atInfo().log(message);
    }

    private List<Path> getDocxFilesInInputFolder() {
        return Arrays.stream(Objects.requireNonNull(inputFolder.toFile().listFiles()))
                .filter(File::isFile)
                .map(File::toPath)
                .filter(this::isValidDocx)
                .toList();
    }

    private boolean isValidDocx(Path path) {
        return DOCX_EXTENSION.matches(path.getFileName()) && !path.getFileName().toString().startsWith("~$");
    }
}