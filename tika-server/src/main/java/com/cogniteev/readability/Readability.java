package com.cogniteev.readability;

import de.jetwick.snacktory.ArticleTextExtractor;
import de.jetwick.snacktory.OutputFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class Readability {



    @Path("/readability")
    @PUT
    @Consumes("text/plain")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ReadabilityResult readability(String source) throws Exception {
        ArticleTextExtractor extractor = new ArticleTextExtractor();
        ReadabilityResult res = new ReadabilityResult();
        Document doc = Jsoup.parse(source);
        extractor.extractContent(res, doc, new OutputFormatter());

        res.setOgAttributes(extractOgAttributes(doc));
        res.setTwitterAttributes(extractTwitterAttributes(doc));

        return res;
    }

    protected Map<String, String> extractOgAttributes(Document doc) {
        Map<String, String> res = new HashMap<String, String>();
        for(Element e: doc.select("head meta[property^=og:]")) {
            res.put(e.attr("property"), e.attr("content"));
        }

        return res;
    }

    protected Map<String, String> extractTwitterAttributes(Document doc) {
        Map<String, String> res = new HashMap<String, String>();
        for(Element e: doc.select("head meta[name^=twitter:]")) {
            res.put(e.attr("name"), e.attr("content"));
        }

        return res;
    }
}
