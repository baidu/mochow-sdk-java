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

import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class SignOptions {
    /**
     * The default sign options, which is {headersToSign:null, timestamp:null, expirationInSeconds:1800}.
     */
    public static final SignOptions DEFAULT = new SignOptions();

    public static final int DEFAULT_EXPIRATION_IN_SECONDS = 1800;

    /**
     * The set of headers to be signed.
     */
    @Setter
    private Set<String> headersToSign = null;

    /**
     * The time until the signature will expire.
     *
     */
    @Setter
    private int expirationInSeconds = DEFAULT_EXPIRATION_IN_SECONDS;

    /**
     * Add the key of headers to be signed.
     *
     * @param headerKey the key of headers to be signed.
     */
    public void addHeadersToSign(String headerKey) {
        if (this.headersToSign == null) {
            headersToSign = new HashSet<String>();
        }
        headersToSign.add(headerKey);
    }

    @Override
    public String toString() {
        return "SignOptions [\n  headersToSign=" + headersToSign + ",\n  expirationInSeconds=" + expirationInSeconds
                + "]";
    }
}