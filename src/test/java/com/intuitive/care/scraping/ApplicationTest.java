package com.intuitive.care.scraping;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.mockito.Mockito.*;

class ApplicationTest {

    @Test
    void testMainExecutesScraperAndTransformer() {
        try (MockedConstruction<PdfScraper> mockScraper = mockConstruction(PdfScraper.class);
             MockedConstruction<DataTransformer> mockTransformer = mockConstruction(DataTransformer.class)) {

            Application.main(new String[]{});

            PdfScraper scraperInstance = mockScraper.constructed().get(0);
            DataTransformer transformerInstance = mockTransformer.constructed().get(0);

            verify(scraperInstance).extract();
            verify(transformerInstance).transform();
        }
    }
}
