package com.nitpeek.io.docx.util;

import com.google.common.jimfs.Jimfs;
import com.nitpeek.io.testutil.FileUtil;
import jakarta.xml.bind.JAXBException;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.analyze.analyzer.LiteralReplacer;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import com.nitpeek.io.SimpleAnnotator;
import com.nitpeek.io.docx.DocxPageSource;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class EasyDocxAnnotatorShould {
    private static final String OUTPUT_TEST_FOLDER = "Out_DocxAnnotator_TestDir";
    private final FileSystem fileSystem = Jimfs.newFileSystem();
    private final Path outputDir = FileUtil.testDir(fileSystem, OUTPUT_TEST_FOLDER);
    private final Translation i18n = new DefaultFallbackEnglishTranslation();

    @Mock private Rule rule;

    @Mock private RuleSetProvider ruleSetProvider;


    @BeforeEach
    void setup() throws IOException {
        Files.createDirectory(outputDir);
        when(ruleSetProvider.getRules()).thenReturn(Set.of(rule));
        when(rule.createAnalyzer()).thenReturn(new LiteralReplacer("line", "replace", true));
    }

    @Test
    void createDocxWithSameContentsInTargetFolder() throws IOException, ReportingException, URISyntaxException, JAXBException, Docx4JException {

        Path inputPath = Paths.get(EasyDocxAnnotatorShould.class.getResource("../TestFile.docx").toURI());
        Path outputPath = SimpleAnnotator.outputPath(inputPath, outputDir);
        try (var input = Files.newInputStream(inputPath)) {
            var docxSource = DocxPageSource.createFrom(input);
            var pagesOriginal = new ListPageConsumer(docxSource).getPages();
            var annotator = new EasyDocxAnnotator(Set.of(ruleSetProvider), i18n);
            annotator.annotateFeatures(inputPath, outputDir);
            var pagesAnnotated = new ListPageConsumer(DocxPageSource.createFrom(Files.newInputStream(outputPath))).getPages();
            assertEquals(pagesOriginal, pagesAnnotated);
        }
    }
}