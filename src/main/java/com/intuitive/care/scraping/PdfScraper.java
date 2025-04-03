package com.intuitive.care.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.intuitive.care.scraping.FileCompressor.compressFiles;

public class PdfScraper {

    private static final String BASE_URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
    private static final String DOWNLOAD_DIRECTORY = "downloads/";
    private static final List<String> TARGET_FILES = List.of("Anexo_I", "Anexo_II");

    public void extract() {
        createDownloadDirectory();

        try {
            Document document = Jsoup.connect(BASE_URL).get();
            document.select("a[href$=.pdf]")
                    .stream()
                    .map(link -> link.absUrl("href"))
                    .filter(this::isTargetFile)
                    .forEach(this::downloadFile);

            compressFiles(DOWNLOAD_DIRECTORY, "anexos.zip");
        } catch (IOException e) {
            System.err.println("Error during web scraping: " + e.getMessage());
        }
    }

    private void createDownloadDirectory() {
        File directory = new File(DOWNLOAD_DIRECTORY);
        if (!directory.exists() && !directory.mkdir()) {
            System.err.println("Failed to create download directory.");
        }
    }

    private boolean isTargetFile(String fileUrl) {
        return TARGET_FILES.stream().anyMatch(fileUrl::contains);
    }

    private void downloadFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            File destination = new File(DOWNLOAD_DIRECTORY + fileName);
            FileUtils.copyURLToFile(new URL(fileUrl), destination);
            System.out.println("Downloaded: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to download file: " + fileUrl);
        }
    }
}
