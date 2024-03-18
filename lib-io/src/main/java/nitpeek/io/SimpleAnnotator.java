package nitpeek.io;

import nitpeek.core.api.report.ReportingException;

import java.nio.file.Path;

public interface SimpleAnnotator {

    String ANNOTATED_PREFIX = "nitpicked_";

    void annotateFeatures(Path inputPdf, Path outputDirectory) throws ReportingException;

    static Path outputPath(Path inputFile, Path outputDirectory) {
        return outputDirectory.resolve(outputDirectory.getFileSystem().getPath(ANNOTATED_PREFIX + inputFile.getFileName()));
    }
}
