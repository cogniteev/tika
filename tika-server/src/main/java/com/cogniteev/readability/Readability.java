package com.cogniteev.readability;

import de.jetwick.snacktory.ArticleTextExtractor;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class Readability {

    @Path("/readability")
    @PUT
    @Consumes("text/plain")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ReadabilityResult readability(String source) throws Exception {
        ArticleTextExtractor extractor = new ArticleTextExtractor();
        ReadabilityResult res = new ReadabilityResult();
        extractor.extractContent(res, source);
        return res;
    }


}
