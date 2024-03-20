package nitpeek.client.console;

import nitpeek.client.DocxAnnotatingApplication;
import nitpeek.client.PdfAnnotatingApplication;
import nitpeek.client.application.Application;
import nitpeek.client.application.OneShotSuspendApplication;
import nitpeek.client.application.PluginListingApplication;
import nitpeek.client.ruleset.Language;
import nitpeek.core.impl.translate.CurrentDefaultLocaleProvider;

import java.nio.file.Path;

public class Main {

    private static final Path WORKING_DIR = Path.of("").toAbsolutePath();

    private static final Path pdfInputFolder = WORKING_DIR.resolveSibling("inputPDF");
    private static final Path pdfOutputFolder = WORKING_DIR.resolveSibling("outputPDF");
    private static final Path docxInputFolder = WORKING_DIR.resolveSibling("inputDOCX");
    private static final Path docxOutputFolder = WORKING_DIR.resolveSibling("outputDOCX");

    public static void main(String[] args) throws Exception {

        var localeProvider = new CurrentDefaultLocaleProvider();
        Application app = new OneShotSuspendApplication(() -> {
            new PluginListingApplication(localeProvider).run();
            new PdfAnnotatingApplication(localeProvider, pdfInputFolder, pdfOutputFolder).run();
            new DocxAnnotatingApplication(localeProvider, docxInputFolder, docxOutputFolder, getLanguage(args)).run();
        }, localeProvider);

        app.run();
    }

    private static Language getLanguage(String[] args) {
        var languageKey = getParameterValue("-l", args);
        if ("DE".equalsIgnoreCase(languageKey)) return Language.GERMAN;
        else return Language.ENGLISH;
    }

    private static String getParameterValue(String parameterSwitch, String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (parameterSwitch.equals(args[i])) return args[i + 1];
        }
        return "";
    }
}