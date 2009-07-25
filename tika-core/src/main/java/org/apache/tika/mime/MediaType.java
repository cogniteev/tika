/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.mime;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Internet media type.
 */
public final class MediaType {

    private static final Map<String, String> NO_PARAMETERS =
        new TreeMap<String, String>();

    private static final Pattern SPECIAL =
        Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]");

    private static final Pattern SPECIAL_OR_WHITESPACE =
        Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]");

    public static final MediaType OCTET_STREAM =
        new MediaType("application", "octet-stream", NO_PARAMETERS);

    public static final MediaType TEXT_PLAIN =
        new MediaType("text", "plain", NO_PARAMETERS);

    public static final MediaType APPLICATION_XML =
        new MediaType("application", "xml", NO_PARAMETERS);

    /**
     * Parses the given string to a media type. The string is expected to be of
     * the form "type/subtype(; parameter=...)*" as defined in RFC 2045.
     * 
     * @param string
     *            media type string to be parsed
     * @return parsed media type, or <code>null</code> if parsing fails
     */
    public static MediaType parse(String string) {
        int colon = string.indexOf(';');
        if (colon != -1 && colon != string.length()-1) {
            String primarySubString = string.substring(0, colon);
            String parameters = string
                    .substring(colon + 1, string.length());

            MediaType type = parseNoParams(primarySubString);
            String[] paramBases = parameters.split(";");
            for (int i = 0; i < paramBases.length; i++) {
                String[] paramToks = paramBases[i].split("=");
                String paramName = paramToks[0].trim();
                String paramValue = paramToks[1].trim();
                type.parameters.put(paramName, paramValue);
            }

            return type;

        } else
            return parseNoParams(string);

    }

    private static MediaType parseNoParams(String string) {
        int slash = string.indexOf('/');
        if (slash != -1) {
            String type = string.substring(0, slash).trim();
            String subtype = string.substring(slash + 1).trim();
            if (type.length() > 0 && subtype.length() > 0) {
                return new MediaType(type, subtype);
            }
        }

        return null;
    }

    private final String type;

    private final String subtype;

    private final SortedMap<String, String> parameters;

    public MediaType(
            String type, String subtype, Map<String, String> parameters) {
        this.type = type.trim().toLowerCase();
        this.subtype = subtype.trim().toLowerCase();
        this.parameters = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            this.parameters.put(
                    entry.getKey().trim().toLowerCase(), entry.getValue());
        }
    }

    public MediaType(String type, String subtype) {
        this(type, subtype, NO_PARAMETERS);
    }

    private static Map<String, String> union(
            Map<String, String> a, Map<String, String> b) {
        if (a.isEmpty()) {
            return b;
        } else if (b.isEmpty()) {
            return a;
        } else {
            Map<String, String> union = new HashMap<String, String>();
            union.putAll(a);
            union.putAll(b);
            return union;
        }
    }

    public MediaType(MediaType type, Map<String, String> parameters) {
        this(type.type, type.subtype, union(type.parameters, parameters));
    }

    public MediaType getBaseType() {
        if (parameters.isEmpty()) {
            return this;
        } else {
            return new MediaType(type, subtype);
        }
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public boolean isSpecializationOf(MediaType that) {
        if (OCTET_STREAM.equals(that)) {
            return true;
        } else if (!type.equals(that.type)) {
            return false;
        } else if (!parameters.entrySet().containsAll(that.parameters.entrySet())) {
            return false;
        } else if (TEXT_PLAIN.equals(that.getBaseType())) {
            return true;
        } else if (APPLICATION_XML.equals(that.getBaseType())
                && subtype.endsWith("+xml")) {
            return true;
        } else {
            return subtype.equals(that.subtype);
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type);
        builder.append('/');
        builder.append(subtype);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.append("; ");
            builder.append(entry.getKey());
            builder.append("=");
            String value = entry.getValue();
            if (SPECIAL_OR_WHITESPACE.matcher(value).find()) {
                builder.append('"');
                builder.append(SPECIAL.matcher(value).replaceAll("\\\\$0"));
                builder.append('"');
            } else {
                builder.append(value);
            }
        }
        return builder.toString();
    }

    public boolean equals(Object object) {
        if (object instanceof MediaType) {
            MediaType that = (MediaType) object;
            return type.equals(that.type)
                && subtype.equals(that.subtype)
                && parameters.equals(that.parameters);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + subtype.hashCode();
        hash = hash * 31 + parameters.hashCode();
        return hash;
    }

}