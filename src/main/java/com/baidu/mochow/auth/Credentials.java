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

import lombok.Builder;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of the Credentials interface that allows callers to pass in the account and apikey
 * in the constructor.
 */
@Builder
public class Credentials {
    /**
     * The user account name.
     */
    @Getter
    private final String account;

    /**
     * The apiKey.
     */
    @Getter
    private final String apiKey;

    public Credentials(String account, String apiKey) {
        checkNotNull(account, "account should not be null.");
        checkArgument(!account.isEmpty(), "account should not be empty.");
        checkNotNull(apiKey, "apiKey should not be null.");
        checkArgument(!apiKey.isEmpty(), "apiKey should not be empty.");
        this.account = account;
        this.apiKey = apiKey;
    }
}
