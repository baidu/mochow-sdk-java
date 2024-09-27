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

package com.baidu.mochow.model;

import java.io.Serializable;

import com.baidu.mochow.http.MochowResponseMetadata;

/**
 * Represents the response from an Mochow service, including the result payload and any response metadata. Mochow response
 * metadata consists primarily of the Mochow request ID, which can be used for debugging purposes when services aren't
 * acting as expected.
 */
public class AbstractMochowResponse implements Serializable {

    protected MochowResponseMetadata metadata = new MochowResponseMetadata();

    public MochowResponseMetadata getMetadata() {
        return this.metadata;
    }
}