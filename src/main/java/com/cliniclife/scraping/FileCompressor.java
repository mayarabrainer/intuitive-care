package com.cliniclife.scraping;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileCompressor {

    public static void compressFile(String filePath, String zipFilePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file)) {

            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, bytesRead);
            }

            zos.closeEntry();
            System.out.println("File compressed: " + zipFilePath);

        } catch (IOException e) {
            System.err.println("Error compressing file: " + e.getMessage());
        }
    }

    public static void compressFiles(String sourceDir, String zipFileName) {
        File directory = new File(sourceDir);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Source directory does not exist or is not a directory: " + sourceDir);
            return;
        }

        File zipFile = new File(directory, zipFileName);
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(directory.toPath())
                 .filter(Files::isRegularFile)
                 .forEach(file -> addFileToZip(zos, file, directory.toPath()));

            System.out.println("Files compressed successfully: " + zipFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error while compressing files: " + e.getMessage());
        }
    }

    private static void addFileToZip(ZipOutputStream zos, Path file, Path basePath) {
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            String zipEntryName = basePath.relativize(file).toString();
            zos.putNextEntry(new ZipEntry(zipEntryName));

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        } catch (IOException e) {
            System.err.println("Error adding file to ZIP: " + file + " - " + e.getMessage());
        }
    }
}
