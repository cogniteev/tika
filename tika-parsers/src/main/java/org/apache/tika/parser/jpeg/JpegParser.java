package org.apache.tika.parser.jpeg;

import org.apache.tika.parser.Parser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.exception.TikaException;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.MetadataException;

public class JpegParser implements Parser {
    /**
     * @deprecated This method will be removed in Apache Tika 1.0.
     */
    public void parse(
            InputStream stream, ContentHandler handler, Metadata metadata)
            throws IOException, SAXException, TikaException {
        Map<String, Object> context = Collections.emptyMap();
        parse(stream, handler, metadata, context);
    }

    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, Map<String, Object> context)
            throws IOException, SAXException, TikaException {
        try {
            com.drew.metadata.Metadata jpegMetadata = JpegMetadataReader.readMetadata(stream);

            Iterator directories = jpegMetadata.getDirectoryIterator();
            while (directories.hasNext()) {
                Directory directory = (Directory) directories.next();
                Iterator tags = directory.getTagIterator();

                while (tags.hasNext()) {
                    Tag tag = (Tag)tags.next();
                    
                    metadata.set(tag.getTagName(), tag.getDescription());
                }
            }
        } catch (JpegProcessingException e) {
            throw new TikaException("Can't read JPEG metadata", e);
        } catch (MetadataException e) {
            throw new TikaException("Can't read JPEG metadata", e);
        }

        XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
    }
}