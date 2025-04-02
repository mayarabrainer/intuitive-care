package com.cliniclife.scraping;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.cliniclife.scraping.FileCompressor.compressFile;

public class DataTransformer {

    private static final String TWO_OR_MORE_SPACES = "\\s{2,}";

    private static final String PDF_FILE = "downloads/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf";
    private static final String CSV_FILE = "downloads/Rol_de_Procedimentos.csv";
    private static final String ZIP_FILE = "downloads/Teste_Mayara.zip";

    public static void main(String[] args) {
        try {
            String pdfText = extractTextFromPDF();

            saveAsCSV(pdfText, CSV_FILE);

            compressFile(CSV_FILE, ZIP_FILE);

            System.out.println("Process completed successfully! ✅");

        } catch (Exception e) {
            System.err.println("Error processing the data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String extractTextFromPDF() throws IOException {
        try (PDDocument document = PDDocument.load(new File(PDF_FILE))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private static void saveAsCSV(String pdfText, String csvPath) throws IOException {
        List<String[]> csvData = new ArrayList<>();

        for (String line : pdfText.split("\n")) {
            line = line.replace("OD", "Seg. Odontológica")
                       .replace("AMB", "Seg. Ambulatorial");

            String[] rowData = line.trim().split(TWO_OR_MORE_SPACES);
            if (rowData.length > 1) {
                csvData.add(rowData);
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvPath))) {
            writer.writeAll(csvData);
        }
    }
}
