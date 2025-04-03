package com.intuitive.care.scraping;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipFile;
import static org.junit.jupiter.api.Assertions.*;

class FileCompressorTest {

    private static final String TEST_DIR = "test_files";
    private static final String TEST_FILE = TEST_DIR + "/test.txt";
    private static final String ZIP_FILE = TEST_DIR + "/test.zip";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_DIR));
        Files.writeString(Paths.get(TEST_FILE), "Sample content");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(Paths.get(TEST_DIR))
             .sorted((a, b) -> b.compareTo(a))
             .map(Path::toFile)
             .forEach(File::delete);
    }

    @Test
    void testCompressFile() {
        FileCompressor.compressFile(TEST_FILE, ZIP_FILE);
        
        File zip = new File(ZIP_FILE);
        assertTrue(zip.exists());
        
        try (ZipFile zipFile = new ZipFile(ZIP_FILE)) {
            assertEquals(1, zipFile.size());
        } catch (IOException e) {
            fail("Error reading ZIP file");
        }
    }

    @Test
    void testCompressFileHandlesMissingFile() {
        assertDoesNotThrow(() -> FileCompressor.compressFile("non_existent.txt", ZIP_FILE));
    }

    @Test
    void testCompressFiles() throws IOException {
        Files.writeString(Paths.get(TEST_DIR + "/test2.txt"), "Another file");
        
        FileCompressor.compressFiles(TEST_DIR, "test.zip");
        File zip = new File(ZIP_FILE);
        assertTrue(zip.exists());
        
        try (ZipFile zipFile = new ZipFile(ZIP_FILE)) {
            assertEquals(3, zipFile.size());
        } catch (IOException e) {
            fail("Error reading ZIP file");
        }
    }
}
