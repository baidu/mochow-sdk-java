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
package com.baidu.mochow.http;

import static com.google.common.base.Preconditions.checkNotNull;

import com.baidu.mochow.client.ClientConfiguration;
import com.baidu.mochow.exception.MochowClientException;
import com.baidu.mochow.exception.MochowServiceException;
import com.baidu.mochow.auth.Credentials;
import com.baidu.mochow.auth.Signer;
import com.baidu.mochow.http.handler.HttpResponseHandler;
import com.baidu.mochow.internal.InternalRequest;
import com.baidu.mochow.model.AbstractMochowResponse;
import com.baidu.mochow.util.HttpUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@ThreadSafe
public class HttpClient {

    /**
     * Logger providing detailed information on requests/responses. Users can enable this LOG to get access to Mochow
     * request IDs for responses, individual requests and parameters sent to Mochow, etc.
     */
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("com.baidu.request");

    /**
     * Logger for more detailed debugging information, that might not be as useful for end users (ex: HTTP client
     * configuration, etc).
     */
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    /**
     * Internal client for sending HTTP requests
     */
    protected CloseableHttpClient httpClient;

    /**
     * Internal async client for sending HTTP requests
     */
    protected CloseableHttpAsyncClient httpAsyncClient;

    /**
     * Client configuration options, such as proxy settings, max retries, etc.
     */
    protected ClientConfiguration config;

    protected Signer signer;

    private HttpClientConnectionManager connectionManager;
    private NHttpClientConnectionManager nioConnectionManager;

    private RequestConfig.Builder requestConfigBuilder;
    private CredentialsProvider credentialsProvider;
    private HttpHost proxyHttpHost;

    private boolean isHttpAsyncPutEnabled = false;

    private static ConcurrentHashMap<String, CloseableHttpAsyncClient> asyncClientMap =
            new ConcurrentHashMap<String, CloseableHttpAsyncClient>();

    private static ConcurrentHashMap<String, NHttpClientConnectionManager> managerMap =
            new ConcurrentHashMap<String, NHttpClientConnectionManager>();

    /**
     * Constructs a new Mochow client using the specified client configuration options (ex: max retry attempts, proxy
     * settings, etc), and request metric collector.
     *
     * @param config Configuration options specifying how this client will communicate with Mochow (ex: proxy settings,
     *               retry count, etc.).
     * @param signer signer used to sign http requests
     * @throws java.lang.IllegalArgumentException If config or signer is null.
     */
    public HttpClient(ClientConfiguration config, Signer signer) {
        checkNotNull(config, "config should not be null.");
        checkNotNull(signer, "signer should not be null.");
        this.config = config;
        this.signer = signer;
        this.connectionManager = this.createHttpClientConnectionManager();
        this.httpClient = this.createHttpClient(this.connectionManager);
        IdleConnectionReaper.registerConnectionManager(this.connectionManager);

        this.requestConfigBuilder = RequestConfig.custom();
        this.requestConfigBuilder.setConnectTimeout(config.getConnectionTimeoutInMillis());
        this.requestConfigBuilder.setStaleConnectionCheckEnabled(true);
        if (config.getLocalAddress() != null) {
            this.requestConfigBuilder.setLocalAddress(config.getLocalAddress());
        }
    }

    /**
     * Constructs a new Mochow Http Client with httpAsyncPutEnabled.
     *
     * @param config                Configuration options specifying how this client will communicate with Mochow (ex: proxy settings,
     *                              retry count, etc.).
     * @param signer                signer used to sign http requests
     * @param isHttpAsyncPutEnabled whether use Async for PUT method.
     */
    public HttpClient(ClientConfiguration config, Signer signer, boolean isHttpAsyncPutEnabled) {
        this(config, signer);
        if (isHttpAsyncPutEnabled) {
            try {
                this.nioConnectionManager = this.createNHttpClientConnectionManager();
                this.httpAsyncClient = this.createHttpAsyncClient(this.nioConnectionManager);
                this.httpAsyncClient.start();
                this.isHttpAsyncPutEnabled = true;
            } catch (IOReactorException e) {
                this.isHttpAsyncPutEnabled = false;
            }
        } else {
            this.isHttpAsyncPutEnabled = false;
        }
    }

    /**
     * Executes the request and returns the result.
     *
     * @param <T>              The type of response
     * @param request          The Mochow request to send to the remote server
     * @param responseClass    A response handler to accept a successful response from the remote server
     * @param responseHandlers A response handler to accept an unsuccessful response from the remote server
     * @return The response from the remote server
     * @throws MochowClientException  If any errors are encountered on the client while making the
     *                                          request or handling the response.
     * @throws MochowServiceException If any errors occurred in Mochow while processing the request.
     */
    public <T extends AbstractMochowResponse> T execute(InternalRequest request, Class<T> responseClass,
                                                        HttpResponseHandler[] responseHandlers) {
        // Apply whatever request options we know how to handle, such as user-agent.
        Credentials credentials = config.getCredentials();
        long delayForNextRetryInMillis = 0;
        for (int attempt = 1; ; ++attempt) {
            HttpRequestBase httpRequest = null;
            CloseableHttpResponse httpResponse = null;
            try {
                // Sign the request if credentials were provided
                if (credentials != null) {
                    this.signer.sign(request, credentials);
                }

                REQUEST_LOGGER.debug("Sending Request: {}", request);

                httpRequest = this.createHttpRequest(request);

                HttpContext httpContext = this.createHttpContext(request);

                if (this.isHttpAsyncPutEnabled && httpRequest.getMethod().equals("PUT")) {
                    Future<HttpResponse> future = httpAsyncClient.execute(HttpAsyncMethods.create(httpRequest),
                            new BasicAsyncResponseConsumer(),
                            httpContext, null);
                    httpResponse = new MochowCloseableHttpResponse(future.get());
                } else {
                    httpResponse = this.httpClient.execute(httpRequest, httpContext);
                }
                HttpUtils.printRequest(httpRequest);
                MochowHttpResponse bceHttpResponse = new MochowHttpResponse(httpResponse);

                T response = responseClass.newInstance();
                for (HttpResponseHandler handler : responseHandlers) {
                    if (handler.handle(bceHttpResponse, response)) {
                        break;
                    }
                }
                // everything is ok
                return response;
            } catch (Exception e) {
                MochowClientException bce;
                if (e instanceof MochowClientException) {
                    bce = (MochowClientException) e;
                } else {
                    bce = new MochowClientException("Unable to execute HTTP request", e);
                }
                delayForNextRetryInMillis =
                        this.getDelayBeforeNextRetryInMillis(httpRequest, bce, attempt, this.config.getRetryPolicy());
                if (delayForNextRetryInMillis < 0) {
                    throw bce;
                }

                LOG.debug("Retriable error detected, will retry in {} ms, attempt number: {}",
                        delayForNextRetryInMillis, attempt);
                try {
                    Thread.sleep(delayForNextRetryInMillis);
                } catch (InterruptedException e1) {
                    throw new MochowClientException("Delay interrupted", e1);
                }
                if (request.getContent() != null) {
                    request.getContent().restart();
                }
                if (httpResponse != null) {
                    try {
                        HttpEntity entity = httpResponse.getEntity();
                        if (entity != null && entity.isStreaming()) {
                            final InputStream instream = entity.getContent();
                            if (instream != null) {
                                instream.close();
                            }
                        }
                    } catch (IOException e1) {
                        LOG.debug("Fail to consume entity.", e1);
                        try {
                            httpResponse.close();
                        } catch (IOException e2) {
                            LOG.debug("Fail to close connection.", e2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Shuts down this HTTP client object, releasing any resources that might be held open. This is an optional method,
     * and callers are not expected to call it, but can if they want to explicitly release any open resources. Once a
     * client has been shutdown, it cannot be used to make more requests.
     */
    public void shutdown() {
        IdleConnectionReaper.removeConnectionManager(this.connectionManager);
        try {
            this.httpClient.close();
        } catch (IOException e) {
            LOG.debug("Fail to close httpClient", e);
        }
        this.connectionManager.shutdown();
    }

    /**
     * The difference between shutdown() is that releasing all resources including nio and idle.
     */
    public void shutdownClean() {
        shutdown();
        IdleConnectionReaper.shutdown();
        if (this.httpAsyncClient != null) {
            try {
                this.httpAsyncClient.close();
            } catch (IOException e) {
                LOG.debug("Fail to close httpAsyncClient", e);
            }
        }
        if (this.nioConnectionManager != null) {
            try {
                this.nioConnectionManager.shutdown();
            } catch (IOException e) {
                LOG.debug("Fail to shutdown nioConnectionManager", e);
            }
        }
    }

    /**
     * Get delay time before next retry.
     *
     * @param method      The current HTTP method being executed.
     * @param exception   The client/service exception from the failed request.
     * @param attempt     The number of times the current request has been attempted.
     * @param retryPolicy The retryPolicy being used.
     * @return The deley time before next retry.
     */
    protected long getDelayBeforeNextRetryInMillis(HttpRequestBase method, MochowClientException exception, int attempt,
                                                   RetryPolicy retryPolicy) {
        int retries = attempt - 1;

        int maxErrorRetry = retryPolicy.getMaxErrorRetry();

        // Immediately fails when it has exceeds the max retry count.
        if (retries >= maxErrorRetry) {
            return -1;
        }

        // Never retry on requests containing non-repeatable entity
        if (method instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) method).getEntity();
            if (entity != null && !entity.isRepeatable()) {
                LOG.debug("Entity not repeatable, stop retrying");
                return -1;
            }
        }

        return Math.min(retryPolicy.getMaxDelayInMillis(),
                retryPolicy.getDelayBeforeNextRetryInMillis(exception, retries));
    }

    /**
     * Create connection manager for http client.
     *
     * @return The connection manager for http client.
     */
    private HttpClientConnectionManager createHttpClientConnectionManager() {
        ConnectionSocketFactory socketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory;
        try {
            sslSocketFactory = new SSLConnectionSocketFactory(SSLContext.getDefault(),
                    SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            throw new MochowClientException("Fail to create SSLConnectionSocketFactory", e);
        }
        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create().register(Protocol.HTTP.toString(), socketFactory)
                        .register(Protocol.HTTPS.toString(), sslSocketFactory).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setDefaultMaxPerRoute(this.config.getMaxConnections());
        connectionManager
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(this.config.getSocketTimeoutInMillis())
                        .setTcpNoDelay(true).build());
        connectionManager.setMaxTotal(this.config.getMaxConnections());
        return connectionManager;
    }

    /**
     * Create connection manager for asynchronous http client.
     *
     * @return Connection manager for asynchronous http client.
     * @throws IOReactorException in case if a non-recoverable I/O error.
     */
    protected NHttpClientConnectionManager createNHttpClientConnectionManager() throws IOReactorException {
        if (managerMap.containsKey(config.getEndpoint())) {
            return managerMap.get(config.getEndpoint());
        }
        ConnectingIOReactor ioReactor =
                new DefaultConnectingIOReactor(IOReactorConfig.custom()
                        .setSoReuseAddress(true)
                        .setIoThreadCount(this.config.getIoThreadCount())
                        .setSoTimeout(this.config.getSocketTimeoutInMillis()).setTcpNoDelay(true).build());
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connectionManager.setDefaultMaxPerRoute(this.config.getMaxConnections());
        connectionManager.setMaxTotal(this.config.getMaxConnections());
        managerMap.putIfAbsent(config.getEndpoint(), connectionManager);
        return connectionManager;
    }

    /**
     * Create http client based on connection manager.
     *
     * @param connectionManager The connection manager setting http client.
     * @return Http client based on connection manager.
     */
    private CloseableHttpClient createHttpClient(HttpClientConnectionManager connectionManager) {
        HttpClientBuilder builder =
                HttpClients.custom().setConnectionManager(connectionManager).disableAutomaticRetries();

        int socketBufferSizeInBytes = this.config.getSocketBufferSizeInBytes();
        if (socketBufferSizeInBytes > 0) {
            builder.setDefaultConnectionConfig(
                    ConnectionConfig.custom().setBufferSize(socketBufferSizeInBytes).build());
        }

        return builder.build();
    }

    /**
     * Create asynchronous http client based on connection manager.
     *
     * @param connectionManager Asynchronous http client connection manager.
     * @return Asynchronous http client based on connection manager.
     */
    protected CloseableHttpAsyncClient createHttpAsyncClient(NHttpClientConnectionManager connectionManager) {
        if (asyncClientMap.containsKey(config.getEndpoint())) {
            return asyncClientMap.get(config.getEndpoint());
        }
        HttpAsyncClientBuilder builder = HttpAsyncClients.custom().setConnectionManager(connectionManager);

        int socketBufferSizeInBytes = this.config.getSocketBufferSizeInBytes();
        if (socketBufferSizeInBytes > 0) {
            builder.setDefaultConnectionConfig(
                    ConnectionConfig.custom().setBufferSize(socketBufferSizeInBytes).build());
        }
        CloseableHttpAsyncClient client = builder.build();
        asyncClientMap.putIfAbsent(config.getEndpoint(), client);
        return client;
    }

    /**
     * Creates HttpClient method object based on the specified request and
     * populates any parameters, headers, etc. from the internal request.
     *
     * @param request The request to convert to an HttpClient method object.
     * @return The converted HttpClient method object with any parameters, headers, etc. from the original request set.
     */
    protected HttpRequestBase createHttpRequest(InternalRequest request) {
        String uri = request.getUri().toASCIIString();
        String encodedParams = HttpUtils.getCanonicalQueryString(request.getParameters(), false);

        if (encodedParams.length() > 0) {
            uri += "?" + encodedParams;
        }

        HttpRequestBase httpRequest;
        long contentLength = -1;
        String contentLengthString = request.getHeaders().get(Headers.CONTENT_LENGTH);
        if (contentLengthString != null) {
            contentLength = Long.parseLong(contentLengthString);
        }
        if (request.getHttpMethod() == HttpMethodName.GET) {
            httpRequest = new HttpGet(uri);
        } else if (request.getHttpMethod() == HttpMethodName.PUT) {
            HttpPut putMethod = new HttpPut(uri);
            httpRequest = putMethod;
            if (request.getContent() != null) {
                putMethod.setEntity(new InputStreamEntity(request.getContent(), contentLength));
            }
        } else if (request.getHttpMethod() == HttpMethodName.POST) {
            HttpPost postMethod = new HttpPost(uri);
            httpRequest = postMethod;
            if (request.getContent() != null) {
                postMethod.setEntity(new InputStreamEntity(request.getContent(), contentLength));
            }
        } else if (request.getHttpMethod() == HttpMethodName.DELETE) {
            httpRequest = new HttpDelete(uri);
        } else if (request.getHttpMethod() == HttpMethodName.HEAD) {
            httpRequest = new HttpHead(uri);
        } else if (request.getHttpMethod() == HttpMethodName.PATCH) {
            HttpPatch patchMethod = new HttpPatch(uri);
            httpRequest = patchMethod;
            if (request.getContent() != null) {
                patchMethod.setEntity(new InputStreamEntity(request.getContent(), contentLength));
            }
        } else {
            throw new MochowClientException("Unknown HTTP method name: " + request.getHttpMethod());
        }

        httpRequest.addHeader(Headers.HOST, HttpUtils.generateHostHeader(request.getUri()));
        // Copy over any other headers already in our request
        for (Entry<String, String> entry : request.getHeaders().entrySet()) {
            /*
             * HttpClient4 fills in the Content-Length header and complains if it's already present, so we skip it here.
             * We also skip the Host header to avoid sending it twice, which will interfere with some signing schemes.
             */
            if (entry.getKey().equalsIgnoreCase(Headers.CONTENT_LENGTH)
                    || entry.getKey().equalsIgnoreCase(Headers.HOST)) {
                continue;
            }

            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }

        checkNotNull(httpRequest.getFirstHeader(Headers.CONTENT_TYPE), Headers.CONTENT_TYPE + " not set");
        return httpRequest;
    }

    /**
     * Creates HttpClient Context object based on the internal request.
     *
     * @param request The internal request.
     * @return HttpClient Context object.
     */
    protected HttpClientContext createHttpContext(InternalRequest request) {
        HttpClientContext context = HttpClientContext.create();
        this.requestConfigBuilder.setExpectContinueEnabled(request.isExpectContinueEnabled())
                .setSocketTimeout(this.config.getSocketTimeoutInMillis());
        if (request.isRedirectsEnabled() != null) {
            // redirect set by user
            this.requestConfigBuilder
                    .setMaxRedirects(request.getMaxRedirects())
                    .setRedirectsEnabled(request.isRedirectsEnabled().booleanValue());
        }
        context.setRequestConfig(this.requestConfigBuilder.build());
        if (this.credentialsProvider != null) {
            context.setCredentialsProvider(this.credentialsProvider);
        }
        return context;
    }
}
