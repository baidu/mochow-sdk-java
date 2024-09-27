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

package com.baidu.mochow.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;
import static com.google.common.base.Preconditions.checkNotNull;

import com.baidu.mochow.http.Headers;
import com.baidu.mochow.internal.InternalRequest;

/**
 * The implementation of Signer for mochow.
 */
public class Signer {
    private static final Logger LOG = LoggerFactory.getLogger(com.baidu.mochow.auth.Signer.class);
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Charset UTF8 = Charset.forName(DEFAULT_ENCODING);

    /**
     * Sign the given request with the given set of credentials. Modifies the passed-in request to apply the signature.
     *
     * @param request     the request to sign.
     * @param credentials the credentials to sign the request with.
     */
    public void sign(InternalRequest request, Credentials credentials) {
        this.sign(request, credentials, null);
    }

    /**
     * Sign the given request with the given set of credentials. Modifies the passed-in request to apply the signature.
     *
     * @param request     the request to sign.
     * @param credentials the credentials to sign the request with.
     * @param options     the options for signing.
     */
    public void sign(InternalRequest request, Credentials credentials, SignOptions options) {
        checkNotNull(request, "request should not be null.");
        checkNotNull(credentials, "credentials should not be null.");
        if (options == null) {
            if (request.getSignOptions() != null) {
                options = request.getSignOptions();
            } else {
                options = SignOptions.DEFAULT;
            }
        }
        String account = credentials.getAccount();
        String apiKey = credentials.getApiKey();
        String authorizationHeader = "Bearer account=" + account + "&api_key=" + apiKey;
        LOG.debug("Authorization:{}", authorizationHeader);
        request.addHeader(Headers.AUTHORIZATION, authorizationHeader);
    }
}
