package com.intuitive.care.scraping;

import org.jsoup.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PdfScraperTest {
    
    PdfScraper pdfScraper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        pdfScraper = new PdfScraper();
    }

    @Test
    void testExtractDownloadsAndCompressesFiles() throws Exception {
        Document mockDocument = mock(Document.class);
        Elements mockElements = mock(Elements.class);
        Element mockElement = mock(Element.class);
        Connection mockConnection = mock(Connection.class);

        when(mockDocument.select("a[href$=.pdf]")).thenReturn(mockElements);
        when(mockElements.stream()).then(invocation -> Stream.of(mockElement));
        when(mockElement.absUrl("href")).thenReturn("https://example.com/Anexo_I.pdf");
        when(mockConnection.get()).thenReturn(mockDocument);

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class);
             MockedStatic<FileUtils> fileUtilsMock = mockStatic(FileUtils.class);
             MockedStatic<FileCompressor> fileCompressorMock = mockStatic(FileCompressor.class)) {

            jsoupMock.when(() -> Jsoup.connect(any(String.class))).thenReturn(mockConnection);

            pdfScraper.extract();

            fileUtilsMock.verify(() -> FileUtils.copyURLToFile(any(URL.class), any(File.class)));
            fileCompressorMock.verify(() -> FileCompressor.compressFiles(any(String.class), any(String.class)));
        }
    }

    @Test
    void testExtractHandlesFileDownloadError() throws Exception {
        Document mockDocument = mock(Document.class);
        Elements mockElements = mock(Elements.class);
        Element mockElement = mock(Element.class);
        Connection mockConnection = mock(Connection.class);

        when(mockDocument.select("a[href$=.pdf]")).thenReturn(mockElements);
        when(mockElements.stream()).then(invocation -> Stream.of(mockElement));
        when(mockElement.absUrl("href")).thenReturn("https://example.com/Anexo_I.pdf");
        when(mockConnection.get()).thenReturn(mockDocument);

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class);
             MockedStatic<FileUtils> fileUtilsMock = mockStatic(FileUtils.class)) {

            jsoupMock.when(() -> Jsoup.connect(any(String.class))).thenReturn(mockConnection);
            fileUtilsMock.when(() -> FileUtils.copyURLToFile(any(URL.class), any(File.class)))
                    .thenThrow(new IOException("File download error"));

            assertDoesNotThrow(() -> pdfScraper.extract());
        }
    }

}
