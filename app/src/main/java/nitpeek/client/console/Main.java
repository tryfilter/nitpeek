package nitpeek.client.console;

import nitpeek.client.DocxAnnotatingApplication;
import nitpeek.client.PdfAnnotatingApplication;
import nitpeek.client.application.PluginListingApplication;
import nitpeek.client.application.SuspendOnExceptionApplication;
import nitpeek.client.ruleset.Language;
import nitpeek.core.impl.translate.CurrentDefaultLocaleProvider;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    private static final Path WORKING_DIR = Path.of("").toAbsolutePath();

    private static final Path pdfInputFolder = WORKING_DIR.resolveSibling("inputPDF");
    private static final Path pdfOutputFolder = WORKING_DIR.resolveSibling("outputPDF");

    private static final Path docxInputFolderEnglish = WORKING_DIR.resolveSibling("inputDOCX_en");
    private static final Path docxInputFolderGerman = WORKING_DIR.resolveSibling("inputDOCX_de");
    private static final Path docxOutputFolder = WORKING_DIR.resolveSibling("outputDOCX");

    public static void main(String[] args) throws IOException {

        var localeProvider = new CurrentDefaultLocaleProvider();
        SuspendOnExceptionApplication app = new SuspendOnExceptionApplication(() -> {
            new PluginListingApplication(localeProvider).run();
            new PdfAnnotatingApplication(localeProvider, pdfInputFolder, pdfOutputFolder).run();
            new DocxAnnotatingApplication(localeProvider, docxInputFolderEnglish, docxOutputFolder, Language.ENGLISH).run();
            new DocxAnnotatingApplication(localeProvider, docxInputFolderGerman, docxOutputFolder, Language.GERMAN).run();
        }, localeProvider);

        app.run();
    }
}