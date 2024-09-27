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
 * Extension of MochowClientException that represents an error response returned by a Mochow service.
 * Receiving an exception of this type indicates that the caller's request was correctly transmitted to the service,
 * but for some reason, the service was not able to process it, and returned an error response instead.
 *
 */
public class MochowServiceException extends MochowClientException {
    private static final long serialVersionUID = 1483785729559154396L;

    /**
     * Indicates who is responsible (if known) for a failed request.
     */
    public enum ErrorType {
        Client,
        Service,
        Unknown
    }

    /**
     * The unique Mochow identifier for the service request the caller made. The Mochow request ID can uniquely identify
     * the Mochow request, and is used for reporting an error to Mochow support team.
     */
    private String requestId;

    /**
     * The Mochow error code represented by this exception.
     */
    private int errorCode;

    /**
     * Indicates (if known) whether this exception was the fault of the caller or the service.
     */
    private ErrorType errorType = ErrorType.Unknown;

    /**
     * The error message as returned by the service.
     */
    private String errorMessage;

    /**
     * The HTTP status code that was returned with this error.
     */
    private int statusCode;

    /**
     * Constructs a new MochowServiceException with the specified message.
     *
     * @param errorMessage An error message describing what went wrong.
     */
    public MochowServiceException(String errorMessage) {
        super(null);
        this.errorMessage = errorMessage;
    }

    /**
     * Constructs a new MochowServiceException with the specified message and exception indicating the root cause.
     *
     * @param errorMessage An error message describing what went wrong.
     * @param cause The root exception that caused this exception to be thrown.
     */
    public MochowServiceException(String errorMessage, Exception cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
    }

    /**
     * Sets the Mochow requestId for this exception.
     *
     * @param requestId The unique identifier for the service request the caller made.
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns the Mochow request ID that uniquely identifies the service request the caller made.
     *
     * @return The Mochow request ID that uniquely identifies the service request the caller made.
     */
    public String getRequestId() {
        return this.requestId;
    }

    /**
     * Sets the Mochow error code represented by this exception.
     *
     * @param errorCode The Mochow error code represented by this exception.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the Mochow error code represented by this exception.
     *
     * @return The Mochow error code represented by this exception.
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Sets the type of error represented by this exception (sender, receiver, or unknown),
     * indicating if this exception was the caller's fault, or the service's fault.
     *
     * @param errorType The type of error represented by this exception (sender or receiver),
     *     indicating if this exception was the caller's fault or the service's fault.
     */
    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    /**
     * Indicates who is responsible for this exception (caller, service, or unknown).
     *
     * @return A value indicating who is responsible for this exception (caller, service, or unknown).
     */
    public ErrorType getErrorType() {
        return this.errorType;
    }

    /**
     * Sets the human-readable error message provided by the service.
     *
     * @param errorMessage the human-readable error message provided by the service.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the human-readable error message provided by the service.
     *
     * @return the human-readable error message provided by the service.
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Sets the HTTP status code that was returned with this service exception.
     *
     * @param statusCode The HTTP status code that was returned with this service exception.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Returns the HTTP status code that was returned with this service exception.
     *
     * @return The HTTP status code that was returned with this service exception.
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getMessage() {
        return this.getErrorMessage() + " (Status Code: " + this.getStatusCode() + "; Error Code: "
                + this.getErrorCode() + "; Request ID: " + this.getRequestId() + ")";
    }
}
