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
package com.baidu.mochow.client;

import com.baidu.mochow.auth.Signer;
import com.baidu.mochow.http.HttpClient;
import com.baidu.mochow.http.Headers;
import com.baidu.mochow.http.handler.HttpResponseHandler;
import com.baidu.mochow.internal.InternalRequest;
import com.baidu.mochow.model.AbstractMochowResponse;
import com.baidu.mochow.util.DateUtils;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.ThreadSafe;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Abstract base class for Mochow service client.
 */
@ThreadSafe
public abstract class AbstractMochowClient {
    /**
     * The default service domain for Mochow.
     */
    public static final String DEFAULT_SERVICE_DOMAIN = "baidubce.com";

    /**
     * The common URL prefix for all Mochow service APIs.
     */
    public static final String URL_PREFIX = "v1";

    /**
     * The default string encoding for all Mochow service APIs.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * The default http request content type for all Mochow service APIs.
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * The endpoint URI for the service.
     */
    @Getter
    private URI endpoint;

    /**
     * Responsible for sending HTTP requests to services.
     */
    @Setter
    @Getter
    private HttpClient client;

    /**
     * The client configuration for this client.
     */
    protected ClientConfiguration config;

    /**
     * A list of handlers for processing HTTP responses from services.
     *
     * @see HttpClient#execute(InternalRequest, Class, HttpResponseHandler[])
     */
    private HttpResponseHandler[] responseHandlers;

    /**
     * Constructs a new AbstractBceClient with the specified client configuration and handlers.
     * <p>
     * The constructor will extract serviceId from the class name automatically.
     * And if there is no endpoint specified in the client configuration, the constructor will create a default one.
     *
     * @param config                the client configuration. The constructor makes a copy of this parameter so that it is
     *                              safe to change the configuration after then.
     * @param responseHandlers      a list of handlers for processing HTTP responses from services. See
     *                              {@link com.baidu.mochow.http.HttpClient#execute(InternalRequest, Class, HttpResponseHandler[])}
     * @param isHttpAsyncPutEnabled whether or not PUT method use Async manner.
     * @throws IllegalStateException    if the class name does not follow the naming convention for BCE clients.
     * @throws IllegalArgumentException if the endpoint specified in the client configuration is not a valid URI.
     */
    public AbstractMochowClient(ClientConfiguration config, HttpResponseHandler[] responseHandlers,
                             boolean isHttpAsyncPutEnabled) {
        this.config = config;
        this.endpoint = this.computeEndpoint();
        this.client = new HttpClient(config, new Signer(), isHttpAsyncPutEnabled);
        this.responseHandlers = responseHandlers;
    }

    /**
     * Equivalent to AbstractBceClient(config, responseHandlers, false)
     *
     * @param config           the client configuration. The constructor makes a copy of this parameter so that it is
     *                         safe to change the configuration after then.
     * @param responseHandlers a list of handlers for processing HTTP responses from services. See
     *                         {@link com.baidu.mochow.http.HttpClient#execute(InternalRequest, Class, HttpResponseHandler[])}
     * @throws IllegalStateException    if the class name does not follow the naming convention for BCE clients.
     * @throws IllegalArgumentException if the endpoint specified in the client configuration is not a valid URI.
     */
    public AbstractMochowClient(ClientConfiguration config, HttpResponseHandler[] responseHandlers) {
        this(config, responseHandlers, true);
    }

    /**
     * Shuts down the client and releases all underlying resources.
     * <p>
     * Invoking this method is NOT a must. Once it is called, no subsequent requests should be made.
     */
    public void shutdown() {
        this.client.shutdown();
    }

    /**
     * Subclasses should invoke this method for sending request to the target service.
     * <p>
     * This method will add "Content-Type" and "Date" to headers with default values if not present.
     *
     * @param request       the request to build up the HTTP request.
     * @param responseClass the response class.
     * @param <T>           the type of response
     * @return the final response object.
     */
    protected <T extends AbstractMochowResponse> T invokeHttpClient(InternalRequest request, Class<T> responseClass) {
        if (!request.getHeaders().containsKey(Headers.CONTENT_TYPE)) {
            request.addHeader(Headers.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        }

        if (!request.getHeaders().containsKey(Headers.DATE)) {
            request.addHeader(Headers.DATE, DateUtils.formatRfc822Date(new Date()));
        }

        return this.client.execute(request, responseClass, this.responseHandlers);
    }

    /**
     * Returns the default target service endpoint.
     * <p>
     * The endpoint will be in the form of "http(s)://<Service ID>[.<Region>].baidubce.com".
     *
     * @return the computed service endpoint
     * @throws IllegalArgumentException if the endpoint specified in the client configuration is not a valid URI.
     */
    private URI computeEndpoint() {
        String endpoint = this.config.getEndpoint();
        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid endpoint." + endpoint, e);
        }
    }
}
