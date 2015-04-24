package com.cogniteev.readability;

import de.jetwick.snacktory.JResult;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class ReadabilityResult extends JResult {
    public Map<String, String> getOgAttributes() {
        return ogAttributes;
    }

    public void setOgAttributes(Map<String, String> ogAttributes) {
        this.ogAttributes = ogAttributes;
    }

    public Map<String, String> ogAttributes;

    public Map<String, String> getTwitterAttributes() {
        return twitterAttributes;
    }

    public void setTwitterAttributes(Map<String, String> twitterAttributes) {
        this.twitterAttributes = twitterAttributes;
    }

    public Map<String, String> twitterAttributes;

}