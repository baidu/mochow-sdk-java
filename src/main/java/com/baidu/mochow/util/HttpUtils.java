/*
 * Copyright 2024 Baidu, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.baidu.mochow.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import com.baidu.mochow.http.Protocol;
import com.baidu.mochow.http.Headers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class HttpUtils {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static BitSet uriUnreservedCharacters = new BitSet();
    private static String[] percentEncodedStrings = new String[256];

    private static final Joiner QUERY_STRING_JOINER = Joiner.on('&');
    private static boolean httpVerbose = Boolean.parseBoolean(System.getProperty("mochow.sdk.http", "false"));

    static {
        for (int i = 'a'; i <= 'z'; i++) {
            uriUnreservedCharacters.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            uriUnreservedCharacters.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            uriUnreservedCharacters.set(i);
        }
        uriUnreservedCharacters.set('-');
        uriUnreservedCharacters.set('.');
        uriUnreservedCharacters.set('_');
        uriUnreservedCharacters.set('~');

        for (int i = 0; i < percentEncodedStrings.length; ++i) {
            percentEncodedStrings[i] = String.format("%%%02X", i);
        }
    }

    /**
     *
     * @param path the path string to normalize.
     * @return the normalized path string.
     * @see #normalize(String)
     */
    public static String normalizePath(String path) {
        return normalize(path).replace("%2F", "/");
    }

    /**
     *
     * @param value the string to normalize.
     * @return the normalized string.
     */
    public static String normalize(String value) {
        try {
            StringBuilder builder = new StringBuilder();
            for (byte b : value.getBytes(DEFAULT_ENCODING)) {
                if (uriUnreservedCharacters.get(b & 0xFF)) {
                    builder.append((char) b);
                } else {
                    builder.append(percentEncodedStrings[b & 0xFF]);
                }
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a host header according to the specified URI. The host header is generated with the same logic used by
     * apache http client, that is, append the port to hostname only if it is not the default port.
     *
     * @param uri the URI
     * @return a host header according to the specified URI.
     */
    public static String generateHostHeader(URI uri) {
        String host = uri.getHost();
        if (isUsingNonDefaultPort(uri)) {
            host += ":" + uri.getPort();
        }
        return host;
    }

    /**
     * Returns true if the specified URI is using a non-standard port (i.e. any port other than 80 for HTTP URIs or any
     * port other than 443 for HTTPS URIs).
     *
     * @param uri the URI
     * @return True if the specified URI is using a non-standard port, otherwise false.
     */
    public static boolean isUsingNonDefaultPort(URI uri) {
        String scheme = uri.getScheme().toLowerCase();
        int port = uri.getPort();
        if (port <= 0) {
            return false;
        }
        if (scheme.equals(Protocol.HTTP.toString())) {
            return port != Protocol.HTTP.getDefaultPort();
        }
        if (scheme.equals(Protocol.HTTPS.toString())) {
            return port != Protocol.HTTPS.getDefaultPort();
        }
        return false;
    }

    public static String getCanonicalQueryString(Map<String, String> parameters, boolean forSignature) {
        if (parameters.isEmpty()) {
            return "";
        }

        List<String> parameterStrings = Lists.newArrayList();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (forSignature && Headers.AUTHORIZATION.equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            String key = entry.getKey();
            checkNotNull(key, "parameter key should not be null");
            String value = entry.getValue();
            if (value == null) {
                if (forSignature) {
                    parameterStrings.add(normalize(key) + '=');
                } else {
                    parameterStrings.add(normalize(key));
                }
            } else {
                parameterStrings.add(normalize(key) + '=' + normalize(value));
            }
        }
        Collections.sort(parameterStrings);

        return QUERY_STRING_JOINER.join(parameterStrings);
    }

    /**
     * Append the given path to the given baseUri.
     * This method will encode the given path but not the given baseUri.
     *
     */
    public static URI appendUri(URI baseUri, String... pathComponents) {
        StringBuilder builder = new StringBuilder(baseUri.toASCIIString());
        for (String path : pathComponents) {
            if (path != null && !path.isEmpty()) {
                path = normalizePath(path);
                if (path.startsWith("/")) {
                    if (builder.charAt(builder.length() - 1) == '/') {
                        builder.setLength(builder.length() - 1);
                    }
                } else {
                    if (builder.charAt(builder.length() - 1) != '/') {
                        builder.append('/');
                    }
                }
                builder.append(path);
            }
        }
        try {
            return new URI(builder.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unexpected error", e);
        }
    }
    
    public static void printRequest(HttpRequestBase request) {
        if (!httpVerbose) {
            return;
        }
        System.out.println("\n-------------> ");
        System.out.println(request.getRequestLine());
        for (Header h : request.getAllHeaders()) {
            System.out.println(h.getName() + " : " + h.getValue());
        }
        RequestConfig config = request.getConfig();
        if (config != null) {
            System.out.println("getConnectionRequestTimeout: "
                    + config.getConnectionRequestTimeout());
            System.out.println("getConnectTimeout: "
                    + config.getConnectTimeout());
            System.out.println("getCookieSpec: " + config.getCookieSpec());
            System.out.println("getLocalAddress: " + config.getLocalAddress());

        }
    }
}