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

/**
 * Map value type enum for MAP field type.
 * Includes scalar types, FLOAT_VECTOR, and ARRAY.
 */
public enum MapValueType {
    BOOL("BOOL"),
    INT8("INT8"),
    UINT8("UINT8"),
    INT16("INT16"),
    UINT16("UINT16"),
    INT32("INT32"),
    UINT32("UINT32"),
    INT64("INT64"),
    UINT64("UINT64"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    DATE("DATE"),
    DATETIME("DATETIME"),
    TIMESTAMP("TIMESTAMP"),
    STRING("STRING"),
    BINARY("BINARY"),
    UUID("UUID"),
    TEXT("TEXT"),
    TEXT_GBK("TEXT_GBK"),
    TEXT_GB18030("TEXT_GB18030"),
    FLOAT_VECTOR("FLOAT_VECTOR"),
    ARRAY("ARRAY");

    private final String value;

    private MapValueType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

