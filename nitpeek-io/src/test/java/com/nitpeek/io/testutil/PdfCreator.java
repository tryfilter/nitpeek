package com.nitpeek.io.testutil;

import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.process.PageSource;
import com.nitpeek.core.impl.process.ListPageConsumer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Unfortunately creating PDFs is quite slow (order of 500ms), at least on first load (pdfbox seems to spend a lot of
 * time on loading fonts and whatnot)
 */
public final class PdfCreator {

    private final List<TextPage> pages;

    public PdfCreator(PageSource pageSource) {
        this.pages = new ListPageConsumer(pageSource).getPages();
    }

    public void createPdf(Path targetFile) throws IOException {
        try (var pdf = new PDDocument()) {
            for (var page : pages) {
                addPage(pdf, page);
            }

            pdf.save(Files.newOutputStream(targetFile));
        }
    }

    private void addPage(PDDocument pdf, TextPage textPage) throws IOException {
        PDPage pdfPage = new PDPage();

        try (PDPageContentStream contentStream = new PDPageContentStream(pdf, pdfPage)) {
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
            printPageContents(contentStream, textPage);
        }

        pdf.addPage(pdfPage);
    }

    private void printPageContents(PDPageContentStream contentStream, TextPage page) throws IOException {

        contentStream.beginText();
        // pdfbox starts outputting text at the bottom of the page
        var lines = page.getLines().reversed();
        for (var line : lines) {
            contentStream.newLineAtOffset(0, 20);
            contentStream.showText(line);
        }

        contentStream.endText();
    }
}
