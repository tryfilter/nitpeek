package nitpeek.client;

import nitpeek.client.application.Application;
import nitpeek.client.ruleset.AllPluginsRuleSetProvider;
import nitpeek.client.plugin.PluginManager;
import nitpeek.client.plugin.ServiceProviderPluginManager;
import nitpeek.client.translation.AllPluginsTranslationProviderFactory;
import nitpeek.core.api.report.ReportingException;
import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.TranslationProvider;
import nitpeek.io.pdf.util.EasyPdfAnnotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;

public final class PdfAnnotatingApplication implements Application {

    private final PluginManager pluginManager = new ServiceProviderPluginManager();
    private final LocaleProvider localeProvider;
    private final Path inputFolder;
    private final Path outputFolder;
    private final Logger log = LoggerFactory.getLogger(PdfAnnotatingApplication.class);

    private static final PathMatcher PDF_EXTENSION = FileSystems.getDefault().getPathMatcher("glob:*.pdf");

    private final TranslationProvider translationProvider = new AllPluginsTranslationProviderFactory(pluginManager).createTranslationProvider();

    public PdfAnnotatingApplication(LocaleProvider localeProvider, Path inputFolder, Path outputFolder) {
        if (!inputFolder.toFile().isDirectory()) throw new IllegalArgumentException("input directory not found: " + inputFolder);
        if (!outputFolder.toFile().isDirectory()) throw new IllegalArgumentException("output directory not found: " + inputFolder);

        this.localeProvider = localeProvider;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    @Override
    public void run() throws ReportingException, IOException {
        var combinedRuleSetProvider = new AllPluginsRuleSetProvider(new ServiceProviderPluginManager());

        var i18n = translationProvider.getTranslation(localeProvider);
        var annotator = new EasyPdfAnnotator(combinedRuleSetProvider, i18n);

        printMessage("Processing PDF files...");
        var files = getPdfFilesInInputFolder();
        for (var inputPdf : files) {
            printMessage("Processing file " + inputPdf);
            annotator.annotateFeatures(inputPdf, outputFolder);
        }
        printMessage("Finished processing " + files.size() + " files.");
    }

    private void printMessage(String message) {
        log.atInfo().log(message);
    }

    private List<Path> getPdfFilesInInputFolder() {
        return Arrays.stream(Objects.requireNonNull(inputFolder.toFile().listFiles()))
                .filter(File::isFile)
                .map(File::toPath)
                .filter(this::isValidPdf)
                .toList();
    }

    private boolean isValidPdf(Path path) {
        return PDF_EXTENSION.matches(path.getFileName());
    }
}