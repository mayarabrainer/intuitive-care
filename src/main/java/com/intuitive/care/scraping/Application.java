package com.intuitive.care.scraping;

public class Application {

    public static void main(String[] args) {
        PdfScraper scraper = new PdfScraper();
        scraper.extract();
        DataTransformer transformer = new DataTransformer();
        transformer.transform();
    }

}
