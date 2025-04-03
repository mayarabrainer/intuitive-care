package com.intuitive.care.scraping;

import com.opencsv.CSVReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataTransformerTest {

    DataTransformer dataTransformer;

    @BeforeEach
    void setUp() {
        dataTransformer = new DataTransformer();
    }

    @Test
    void testTransformCreatesCSV() throws Exception {
        try (MockedStatic<PDDocument> pdDocumentMock = mockStatic(PDDocument.class)) {

            PDDocument mockDocument = mock(PDDocument.class);
            PDFTextStripper mockStripper = mock(PDFTextStripper.class);

            pdDocumentMock.when(() -> PDDocument.load((File) any())).thenReturn(mockDocument);
            when(mockStripper.getText(mockDocument)).thenReturn("DDD  Telefone\n11    40028922");

            dataTransformer.transform();

            try (CSVReader reader = new CSVReader(new FileReader("downloads/Rol_de_Procedimentos.csv"))) {
                List<String[]> records = reader.readAll();
                assertFalse(records.isEmpty());
            }
        }
    }

    @Test
    void testTransformHandlesErrors() {
        try (MockedStatic<FileCompressor> fileCompressorMock = mockStatic(FileCompressor.class)) {
            fileCompressorMock.when(() -> FileCompressor.compressFile(any(), any()))
                    .thenThrow(new RuntimeException("Compression error"));
            
            assertDoesNotThrow(() -> dataTransformer.transform());
        }
    }
}
