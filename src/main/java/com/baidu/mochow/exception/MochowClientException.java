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
package com.baidu.mochow.exception;

/**
 * Base exception class for any errors that occur on the client side when attempting to access a Mochow service API.
 *
 * @see MochowClientException
 */
public class MochowClientException extends RuntimeException {
    private static final long serialVersionUID = -9085416005820812953L;

    /**
     * Constructs a new MochowClientException with the specified detail message.
     *
     * @param message the detail error message.
     */
    public MochowClientException(String message) {
        super(message);
    }

    /**
     * Constructs a new MochowClientException with the specified detail message and the underlying cause.
     *
     * @param message the detail error message.
     * @param cause   the underlying cause of this exception.
     */
    public MochowClientException(String message, Throwable cause) {
        super(message, cause);
    }
}