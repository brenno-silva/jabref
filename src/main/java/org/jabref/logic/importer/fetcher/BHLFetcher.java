package org.jabref.logic.importer.fetcher;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.http.client.utils.URIBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.jabref.logic.importer.FetcherException;
import org.jabref.logic.importer.Parser;
import org.jabref.logic.importer.SearchBasedParserFetcher;
import org.jabref.logic.importer.fetcher.transformers.DefaultQueryTransformer;
import org.jabref.logic.util.OS;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BHLFetcher implements SearchBasedParserFetcher {

    private static final String BASIC_SEARCH_URL = "https://www.biodiversitylibrary.org/api3?";

    @Override
    public Parser getParser() {

        return inputStream -> {
            String response = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining(OS.NEWLINE));
            JSONObject jsonObject = new JSONObject(response);

            JSONArray results = jsonObject.getJSONArray("Result");
            List<BibEntry> entries = new ArrayList<>();

            for (Object jsonItem : results)
                entries.add(jsonToBibEntry((JSONObject) jsonItem));

            return entries;
        };
    }

    @Override
    public URL getURLForQuery(QueryNode luceneQuery) throws URISyntaxException, MalformedURLException, FetcherException {
        URIBuilder uriBuilder = new URIBuilder(BASIC_SEARCH_URL);
        uriBuilder.addParameter("op", "PublicationSearch");
        uriBuilder.addParameter("searchterm", new DefaultQueryTransformer().transformLuceneQuery(luceneQuery).orElse(""));
        uriBuilder.addParameter("searchtype", "C");
        uriBuilder.addParameter("apikey", "140a383e-2a79-40c1-ace0-951036f38af9");
        uriBuilder.addParameter("format", "json");

        return uriBuilder.build().toURL();
    }

    @Override
    public String getName() {
        return "Biodiversity Heritage Library";
    }

    private BibEntry jsonToBibEntry(JSONObject resultData) {
        BibEntry entry = new BibEntry();

        if (resultData.getString("BHLType").equals("Item"))
            entry.setField(StandardField.URL, resultData.getString("ItemUrl"));
        else
            entry.setField(StandardField.URL, resultData.getString("PartUrl"));

        if (resultData.has("Date"))
            entry.setField(StandardField.YEAR, resultData.getString("Date"));
        else if (resultData.has("PublicationDate"))
            entry.setField(StandardField.YEAR, resultData.getString("PublicationDate"));

        entry.setField(StandardField.TITLE, resultData.getString("Title"));
        entry.setField(StandardField.TYPE, resultData.getString("Genre"));

        return entry;
    }
}
