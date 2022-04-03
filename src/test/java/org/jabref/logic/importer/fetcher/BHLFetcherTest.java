package org.jabref.logic.importer.fetcher;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FetcherTest
public class BHLFetcherTest {
    BHLFetcher bhlFetcher;

    @BeforeEach
    void setUp() {
        bhlFetcher = new BHLFetcher();
    }

    @Test
    public void searchByString() throws Exception {
        BibEntry searchMock = new BibEntry();

        searchMock.setField(StandardField.URL, "https://www.biodiversitylibrary.org/part/69838");
        searchMock.setField(StandardField.TITLE, "Field notes on the land birds of the Galapagos Islands, and of Cocos Island,Costa Rica");
        searchMock.setField(StandardField.YEAR, "1919");
        searchMock.setField(StandardField.TYPE, "Article");

        List<BibEntry> entries;

        entries = bhlFetcher.performSearch("cocos island costa rica birds");
        assertTrue(entries.contains(searchMock));

        entries = bhlFetcher.performSearch("my little poney");
        assertTrue(entries.isEmpty());
    }

    @Test
    public void getDatabaseName() throws Exception {
        assertEquals("Biodiversity Heritage Library", bhlFetcher.getName());
    }
}
