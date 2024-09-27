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

package com.baidu.mochow.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServerErrorCode {
    INTERNAL_ERROR(1),
    INVALID_PARAMETER(2),
    INVALID_HTTP_URL(10),
    INVALID_HTTP_HEADER(11),
    INVALID_HTTP_BODY(12),
    MISS_SSL_CERTIFICATES(13),

    USER_NOT_EXIST(20),
    USER_ALREADY_EXIST(21),
    ROLE_NOT_EXIST(22),
    ROLE_ALREADY_EXIST(23),
    AUTHENTICATION_FAILED(24),
    PERMISSION_DENIED(25),

    DB_NOT_EXIST(50),
    DB_ALREADY_EXIST(51),
    DB_TOO_MANY_TABLES(52),
    DB_NOT_EMPTY(53),

    INVALID_TABLE_SCHEMA(60),
    INVALID_PARTITION_PARAMETERS(61),
    TABLE_TOO_MANY_FIELDS(62),
    TABLE_TOO_MANY_FAMILIES(63),
    TABLE_TOO_MANY_PRIMARY_KEYS(64),
    TABLE_TOO_MANY_PARTITION_KEYS(65),
    TABLE_TOO_MANY_VECTOR_FIELDS(66),
    TABLE_TOO_MANY_INDEXES(67),
    DYNAMIC_SCHEMA_ERROR(68),
    TABLE_NOT_EXIST(69),
    TABLE_ALREADY_EXIST(70),
    INVALID_TABLE_STATE(71),
    TABLE_NOT_READY(72),
    ALIAS_NOT_EXIST(73),
    ALIAS_ALREADY_EXIST(74),

    FIELD_NOT_EXIST(80),
    FIELD_ALREADY_EXIST(81),
    VECTOR_FIELD_NOT_EXIST(82),

    INVALID_INDEX_SCHEMA(90),
    INDEX_NOT_EXIST(91),
    INDEX_ALREADY_EXIST(92),
    INDEX_DUPLICATED(93),
    INVALID_INDEX_STATE(94),

    PRIMARY_KEY_DUPLICATED(100),
    ROW_KEY_NOT_FOUND(101);

    private final int value;

    private ServerErrorCode(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }
}
