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

package com.baidu.mochow.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.baidu.mochow.model.enums.ElementType;
import com.baidu.mochow.model.enums.FieldType;
import com.baidu.mochow.model.enums.MapKeyType;
import com.baidu.mochow.model.enums.MapValueType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Field {
    private String fieldName;
    private FieldType fieldType;
    private Boolean primaryKey;
    private Boolean partitionKey;
    private Boolean autoIncrement;
    private Boolean notNull;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int dimension;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private ElementType elementType;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int maxCapacity;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private MapKeyType keyType;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private MapValueType valueType;
}