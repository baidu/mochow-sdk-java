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

package com.baidu.mochow.internal;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.NotThreadSafe;

import com.baidu.mochow.auth.SignOptions;
import com.baidu.mochow.http.HttpMethodName;

/**
 * Represents a request being sent to a Mochow Service, including the
 * parameters being sent as part of the request, the endpoint to which the
 * request should be sent, etc.
 */
@NotThreadSafe
public class InternalRequest {

    /**
     * Map of the parameters being sent as part of this request.
     */
    @Getter
    private Map<String, String> parameters = Maps.newHashMap();

    /**
     * Map of the headers included in this request
     */
    @Getter
    private Map<String, String> headers = Maps.newHashMap();

    /**
     * The service endpoint to which this request should be sent
     */
    @Getter
    private URI uri;

    /**
     * The HTTP method to use when sending this request.
     */
    @Getter
    private HttpMethodName httpMethod;

    /**
     * An optional stream from which to read the request payload.
     */
    @Setter
    @Getter
    private RestartableInputStream content;

    private SignOptions signOptions;

    private boolean expectContinueEnabled;

    private Boolean redirectsEnabled = null;

    private int maxRedirects = 1;

    public InternalRequest(HttpMethodName httpMethod, URI uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }

    public SignOptions getSignOptions() {
        return this.signOptions;
    }

    public void setSignOptions(SignOptions signOptions) {
        this.signOptions = signOptions;
    }

    public boolean isExpectContinueEnabled() {
        return this.expectContinueEnabled;
    }

    public void setExpectContinueEnabled(boolean expectContinueEnabled) {
        this.expectContinueEnabled = expectContinueEnabled;
    }

    public int getMaxRedirects() {
        return this.maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public Boolean isRedirectsEnabled() {
        return redirectsEnabled;
    }

    public void setRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    @Override
    public String toString() {
        return "InternalRequest [httpMethod=" + this.httpMethod + ", uri="  + this.uri + ", "
               + "expectContinueEnabled=" + this.expectContinueEnabled + ", "
               + "parameters=" + this.parameters + ", " + "headers=" + this.headers + "]";
    }
}
