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

import java.io.InputStream;

import org.apache.http.HttpStatus;

import com.baidu.mochow.http.MochowErrorResponse;
import com.baidu.mochow.exception.MochowServiceException;
import com.baidu.mochow.exception.MochowServiceException.ErrorType;
import com.baidu.mochow.http.MochowHttpResponse;
import com.baidu.mochow.model.AbstractMochowResponse;
import com.baidu.mochow.util.JsonUtils;

/**
 * HTTP error response handler for Baidu Mochow responses.
 */
public class MochowErrorResponseHandler implements HttpResponseHandler {
    @Override
    public boolean handle(MochowHttpResponse httpResponse, AbstractMochowResponse response) throws Exception {
        if (httpResponse.getStatusCode() / 100 == HttpStatus.SC_OK / 100) {
            return false;
        }
        MochowServiceException bse = null;
        InputStream content = httpResponse.getContent();
        if (content != null) {
            MochowErrorResponse bceErrorResponse = JsonUtils.loadFrom(content, MochowErrorResponse.class);
            if (bceErrorResponse.getMsg() != null) {
                bse = new MochowServiceException(bceErrorResponse.getMsg());
                bse.setErrorCode(bceErrorResponse.getCode());
                bse.setRequestId(bceErrorResponse.getRequestId());
            }
            content.close();
        }
        if (bse == null) {
            bse = new MochowServiceException(httpResponse.getStatusText());
            bse.setRequestId(response.getMetadata().getRequestID());
        }
        bse.setStatusCode(httpResponse.getStatusCode());
        if (bse.getStatusCode() >= 500) {
            bse.setErrorType(ErrorType.Service);
        } else {
            bse.setErrorType(ErrorType.Client);
        }
        throw bse;
    }
}
