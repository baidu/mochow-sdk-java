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
package com.baidu.mochow.http.handler;

import com.baidu.mochow.http.MochowResponseMetadata;
import com.baidu.mochow.http.MochowHttpResponse;
import com.baidu.mochow.http.Headers;
import com.baidu.mochow.model.AbstractMochowResponse;

/**
 * HTTP response handler for Baidu Mochow responses. Provides common utilities that other specialized Mochow response
 * handlers need to share such as pulling common response metadata (ex: request IDs) out of headers.
 */
public class MochowMetadataResponseHandler implements HttpResponseHandler {
    @Override
    public boolean handle(MochowHttpResponse httpResponse, AbstractMochowResponse response) throws Exception {
        MochowResponseMetadata metadata = response.getMetadata();
        metadata.setRequestID(httpResponse.getHeader(Headers.REQUEST_ID));
        metadata.setContentLength(httpResponse.getHeaderAsLong(Headers.CONTENT_LENGTH));
        metadata.setContentType(httpResponse.getHeader(Headers.CONTENT_TYPE));
        return false;
    }
}
