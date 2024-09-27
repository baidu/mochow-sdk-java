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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Row {
    private Map<String, Object> fields;

    public Row() {
    }

    public Row(Builder builder) {
        this.fields = builder.fields;
    }

    public Object getField(String fieldName) {
        return this.fields.get(fieldName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Object> fields;

        public Builder() {
            this.fields = new HashMap<>();
        }

        public Builder addField(RowField field) {
            this.fields.put(field.getName(), field.getValue());
            return this;
        }

        public Row build() {
            return new Row(this);
        }
    }
}