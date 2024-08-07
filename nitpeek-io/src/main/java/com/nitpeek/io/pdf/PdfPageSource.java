package com.nitpeek.io.pdf;

import com.nitpeek.core.api.process.PageConsumer;
import com.nitpeek.core.api.process.PageSource;
import com.nitpeek.core.api.report.ReportingException;
import com.nitpeek.core.api.report.ReportingException.Problem;
import com.nitpeek.core.impl.process.StringPageSource;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class PdfPageSource implements PageSource {

    private final PageSource memoryPageSource;

    @Override
    public <R> R dischargeTo(PageConsumer<R> consumer) {
        return memoryPageSource.dischargeTo(consumer);
    }

    /**
     * @param input representing a valid PDF file. The InputStream is not closed by this method.
     * @return a page source containing all pages of the provided PDF
     */
    public static PdfPageSource createFrom(InputStream input) throws ReportingException {
        try (var randomAccess = new RandomAccessReadBuffer(input);
             var pdf = Loader.loadPDF(randomAccess)) {
            return new PdfPageSource(toMemoryPageSource(pdf));
        } catch (IOException e) {
            throw new ReportingException("Unable to read PDF", e, Problem.INPUT);
        }
    }

    PdfPageSource(PDDocument pdf) throws ReportingException {
        try {
            this.memoryPageSource = toMemoryPageSource(pdf);
        } catch (IOException e) {
            throw new ReportingException("Unable to create PageSource from PDF", e, Problem.INPUT);
        }
    }

    PdfPageSource(PageSource pageSource) {
        this.memoryPageSource = pageSource;
    }

    private static PageSource toMemoryPageSource(PDDocument pdf) throws IOException {
        int pageCount = pdf.getNumberOfPages();
        final List<String> pages = new ArrayList<>(pageCount);

        for (int page = 1; page <= pageCount; page++) {
            pages.add(extractPage(page, pdf));
        }

        return new StringPageSource(pages);
    }

    private static String extractPage(int pageNumber, PDDocument pdf) throws IOException {

        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);

        textStripper.setStartPage(pageNumber);
        textStripper.setEndPage(pageNumber);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Writer writeBytes = new PrintWriter(new BufferedOutputStream(bytes), false, StandardCharsets.UTF_8);

        textStripper.writeText(pdf, writeBytes);
        writeBytes.flush();

        return bytes.toString();
    }
}