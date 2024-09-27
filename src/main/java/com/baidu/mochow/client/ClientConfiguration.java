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

import java.net.InetAddress;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.NotThreadSafe;

import com.baidu.mochow.http.Protocol;
import com.baidu.mochow.auth.Credentials;
import com.baidu.mochow.http.RetryPolicy;

/**
 * Basic client configurations for Mochow clients.
 */
@NotThreadSafe
@Getter
@Setter
public class ClientConfiguration {

    /**
     * The default timeout for creating new connections.
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT_IN_MILLIS = 50 * 1000;

    /**
     * The default timeout for reading from a connected socket.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT_IN_MILLIS = 50 * 1000;

    /**
     * The default max connection pool size.
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 50;

    /**
     * The default protocol.
     */
    public static Protocol defaultProtocol = Protocol.HTTP;

    /**
     * The retry policy for failed requests.
     */
    private RetryPolicy retryPolicy = RetryPolicy.DEFAULT_RETRY_POLICY;

    /**
     * The optional local address to bind when connecting to Mochow services.
     */
    private InetAddress localAddress;

    /**
     * The protocol (HTTP/HTTPS) to use when connecting to Mochow services.
     */
    private Protocol protocol = Protocol.HTTP;

    /**
     * The maximum number of open HTTP connections.
     */
    private int maxConnections = ClientConfiguration.DEFAULT_MAX_CONNECTIONS;

    /**
     * The socket timeout (SO_TIMEOUT) in milliseconds, which is a maximum period inactivity between two consecutive
     * data packets. A value of 0 means infinity, and is not recommended.
     */
    private int socketTimeoutInMillis = ClientConfiguration.DEFAULT_SOCKET_TIMEOUT_IN_MILLIS;

    /**
     * The connection timeout in milliseconds. A value of 0 means infinity, and is not recommended.
     */
    private int connectionTimeoutInMillis = ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT_IN_MILLIS;

    /**
     * The optional size (in bytes) for the low level TCP socket buffer. This is an advanced option for advanced users
     * who want to tune low level TCP parameters to try and squeeze out more performance. Ignored if not positive.
     */
    private int socketBufferSizeInBytes = 0;

    /**
     * The service endpoint URL to which the client will connect.
     */
    private String endpoint = null;

    /**
     * The Mochow credentials used by the client to sign HTTP requests.
     */
    private Credentials credentials = null;

    /**
     * defalut io thread count
     */
    private int ioThreadCount = Runtime.getRuntime().availableProcessors();

    // Initialize DEFAULT_USER_AGENT
    static {
        String language = System.getProperty("user.language");
        if (language == null) {
            language = "";
        }
    }

    /**
     * Constructs a new ClientConfiguration instance with default settings.
     */
    public ClientConfiguration() {
    }

    /**
     * Constructs a new ClientConfiguration instance with the same settings as the specified configuration.
     *
     * @param other the configuration to copy settings from.
     */
    public ClientConfiguration(ClientConfiguration other) {
        this.connectionTimeoutInMillis = other.connectionTimeoutInMillis;
        this.maxConnections = other.maxConnections;
        this.ioThreadCount = other.ioThreadCount;
        this.retryPolicy = other.retryPolicy;
        this.localAddress = other.localAddress;
        this.protocol = other.protocol;
        this.socketTimeoutInMillis = other.socketTimeoutInMillis;
        this.socketBufferSizeInBytes = other.socketBufferSizeInBytes;
        this.endpoint = other.endpoint;
        this.credentials = other.credentials;
    }

    /**
     * Constructs a new ClientConfiguration instance with the same settings as the specified configuration.
     * This constructor is used to create a client configuration from one SDK to another SDK. e.g. from VOD to BOS.
     * In this case endpoint should be changed while other attributes keep same.
     *
     * @param other    the configuration to copy settings from.
     * @param endpoint the endpoint
     */
    public ClientConfiguration(ClientConfiguration other, String endpoint) {
        this.endpoint = endpoint;
        this.connectionTimeoutInMillis = other.connectionTimeoutInMillis;
        this.maxConnections = other.maxConnections;
        this.ioThreadCount = other.ioThreadCount;
        this.retryPolicy = other.retryPolicy;
        this.localAddress = other.localAddress;
        this.protocol = other.protocol;
        this.socketTimeoutInMillis = other.socketTimeoutInMillis;
        this.socketBufferSizeInBytes = other.socketBufferSizeInBytes;
        this.credentials = other.credentials;
    }

    /**
     * Sets the protocol (HTTP/HTTPS) to use when connecting to Mochow services.
     *
     * @param protocol the protocol (HTTP/HTTPS) to use when connecting to Mochow services.
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol == null ? ClientConfiguration.defaultProtocol : protocol;
    }

    /**
     * Sets the protocol (HTTP/HTTPS) to use when connecting to Mochow services, and returns the updated configuration
     * instance.
     *
     * @param protocol the protocol (HTTP/HTTPS) to use when connecting to Mochow services.
     * @return the updated configuration instance.
     */
    public ClientConfiguration withProtocol(Protocol protocol) {
        this.setProtocol(protocol);
        return this;
    }

    /**
     * Sets the maximum number of open HTTP connections.
     *
     * @param maxConnections the maximum number of open HTTP connections.
     * @throws IllegalArgumentException if maxConnections is negative.
     */
    public void setMaxConnections(int maxConnections) {
        checkArgument(maxConnections >= 0, "maxConnections should not be negative.");
        this.maxConnections = maxConnections;
    }

    /**
     * Sets the maximum number of open HTTP connections, and returns the updated configuration instance.
     *
     * @param maxConnections the maximum number of open HTTP connections.
     * @return the updated configuration instance.
     * @throws IllegalArgumentException if maxConnections is negative.
     */
    public ClientConfiguration withMaxConnections(int maxConnections) {
        this.setMaxConnections(maxConnections);
        return this;
    }

    /**
     * Sets the maximum number of open io thread.
     *
     * @param ioThreadCount the maximum number of open HTTP connections.
     * @throws IllegalArgumentException if ioThreadCount is negative.
     */
    public void setIoThreadCount(int ioThreadCount) {
        checkArgument(ioThreadCount >= 0, "ioThreadCount should not be negative.");
        this.ioThreadCount = ioThreadCount;
    }

    /**
     * Sets the maximum number of io thread, and returns the updated configuration instance.
     *
     * @param ioThreadCount the maximum number of io thread.
     * @return the updated configuration instance.
     * @throws IllegalArgumentException if ioThreadCount is negative.
     */
    public ClientConfiguration withIoThreadCount(int ioThreadCount) {
        this.setIoThreadCount(ioThreadCount);
        return this;
    }

    /**
     * Sets the optional local address to bind when connecting to Mochow services, and returns the updated configuration
     * instance.
     *
     * @param localAddress the optional local address to bind when connecting to Mochow services.
     * @return the updated configuration instance.
     */
    public ClientConfiguration withLocalAddress(InetAddress localAddress) {
        this.setLocalAddress(localAddress);
        return this;
    }

    /**
     * Sets the retry policy for failed requests.
     *
     * @param retryPolicy the retry policy for failed requests.
     */
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy == null ? RetryPolicy.DEFAULT_RETRY_POLICY : retryPolicy;
    }

    /**
     * Sets the retry policy for failed requests, and returns the updated configuration instance.
     *
     * @param retryPolicy the retry policy for failed requests.
     * @return the updated configuration instance.
     */
    public ClientConfiguration withRetryPolicy(RetryPolicy retryPolicy) {
        this.setRetryPolicy(retryPolicy);
        return this;
    }

    /**
     * Sets the socket timeout (SO_TIMEOUT) in milliseconds, which is a maximum period inactivity between two
     * consecutive data packets. A value of 0 means infinity, and is not recommended.
     *
     * @param socketTimeoutInMillis the socket timeout (SO_TIMEOUT) in milliseconds.
     * @throws IllegalArgumentException if socketTimeoutInMillis is negative.
     */
    public void setSocketTimeoutInMillis(int socketTimeoutInMillis) {
        checkArgument(socketTimeoutInMillis >= 0, "socketTimeoutInMillis should not be negative.");
        this.socketTimeoutInMillis = socketTimeoutInMillis;
    }

    /**
     * Sets the socket timeout (SO_TIMEOUT) in milliseconds, which is a maximum period inactivity between two
     * consecutive data packets, and returns the updated configuration instance. A value of 0 means infinity, and is not
     * recommended.
     *
     * @param socketTimeoutInMillis the socket timeout (SO_TIMEOUT) in milliseconds.
     * @return the updated configuration instance.
     * @throws IllegalArgumentException if socketTimeoutInMillis is negative.
     */
    public ClientConfiguration withSocketTimeoutInMillis(int socketTimeoutInMillis) {
        this.setSocketTimeoutInMillis(socketTimeoutInMillis);
        return this;
    }

    /**
     * Sets the connection timeout in milliseconds. A value of 0 means infinity, and is not recommended.
     *
     * @param connectionTimeoutInMillis the connection timeout in milliseconds.
     * @throws IllegalArgumentException if connectionTimeoutInMillis is negative.
     */
    public void setConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
        checkArgument(connectionTimeoutInMillis >= 0, "connectionTimeoutInMillis should not be negative.");
        this.connectionTimeoutInMillis = connectionTimeoutInMillis;
    }

    /**
     * Sets the connection timeout in milliseconds, and returns the updated configuration instance. A value of 0 means
     * infinity, and is not recommended.
     *
     * @param connectionTimeoutInMillis the connection timeout in milliseconds.
     * @return the updated configuration instance.
     * @throws IllegalArgumentException if connectionTimeoutInMillis is negative.
     */
    public ClientConfiguration withConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
        this.setConnectionTimeoutInMillis(connectionTimeoutInMillis);
        return this;
    }

    /**
     * Sets the optional size (in bytes) for the low level TCP socket buffer, and returns the updated configuration
     * instance. This is an advanced option for advanced users who want to tune low level TCP parameters to try and
     * squeeze out more performance. Ignored if not positive.
     *
     * @param socketBufferSizeInBytes the optional size (in bytes) for the low level TCP socket buffer.
     * @return the updated configuration instance.
     */
    public ClientConfiguration withSocketBufferSizeInBytes(int socketBufferSizeInBytes) {
        this.setSocketBufferSizeInBytes(socketBufferSizeInBytes);
        return this;
    }

    /**
     * Returns the service endpoint URL to which the client will connect.
     *
     * @return the service endpoint URL to which the client will connect.
     */
    public String getEndpoint() {
        String url = this.endpoint;
        // if the set endpoint does not contain a protocol, append protocol to head of it
        if (this.endpoint != null && this.endpoint.length() > 0
                && endpoint.indexOf("://") < 0) {
            url = protocol.toString().toLowerCase() + "://" + endpoint;
        }
        return url;
    }

    /**
     * Sets the service endpoint URL to which the client will connect.
     *
     * @param endpoint the service endpoint URL to which the client will connect.
     * @throws IllegalArgumentException if endpoint is not a valid URL.
     * @throws NullPointerException     if endpoint is null.
     */
    public void setEndpoint(String endpoint) {
        checkNotNull(endpoint, "endpoint should not be null.");

        this.endpoint = endpoint;
    }

    /**
     * Sets the service endpoint URL to which the client will connect, and returns the updated configuration instance.
     *
     * @param endpoint the service endpoint URL to which the client will connect.
     * @return the updated configuration instance.
     * @throws IllegalArgumentException if endpoint is not a valid URL.
     * @throws NullPointerException     if endpoint is null.
     */
    public ClientConfiguration withEndpoint(String endpoint) {
        this.setEndpoint(endpoint);
        return this;
    }

    /**
     * Sets the Mochow credentials used by the client to sign HTTP requests.
     *
     * @param credentials the Mochow credentials used by the client to sign HTTP requests.
     * @throws NullPointerException if credentials is null.
     */
    public void setCredentials(Credentials credentials) {
        checkNotNull(credentials, "credentials should not be null.");
        this.credentials = credentials;
    }

    /**
     * Sets the Mochow credentials used by the client to sign HTTP requests, and returns the updated configuration
     * instance.
     *
     * @param credentials the Mochow credentials used by the client to sign HTTP requests.
     * @return the updated configuration instance.
     * @throws NullPointerException if credentials is null.
     */
    public ClientConfiguration withCredentials(Credentials credentials) {
        this.setCredentials(credentials);
        return this;
    }

    @Override
    public String toString() {
        return "ClientConfiguration [ \n"
                + ", retryPolicy=" + retryPolicy + ", \n  localAddress="
                + localAddress + ", \n  protocol=" + protocol + ", \n"
                + "maxConnections=" + maxConnections + ", \n ioThreadCount="
                + ioThreadCount + ", \n  socketTimeoutInMillis="
                + socketTimeoutInMillis + ", \n  connectionTimeoutInMillis="
                + connectionTimeoutInMillis + ", \n  socketBufferSizeInBytes="
                + socketBufferSizeInBytes + ", \n  endpoint=" + endpoint
                + ", \n  credentials=" + credentials + "]\n";
    }
}