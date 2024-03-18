package nitpeek.client.console;

import nitpeek.client.DocxAnnotatingApplication;
import nitpeek.client.PdfAnnotatingApplication;
import nitpeek.client.application.Application;
import nitpeek.client.application.OneShotSuspendApplication;
import nitpeek.client.application.PluginListingApplication;
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
            new DocxAnnotatingApplication(localeProvider, docxInputFolder, docxOutputFolder).run();
        }, localeProvider);

        app.run();
    }
}
