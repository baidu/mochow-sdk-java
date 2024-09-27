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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Schema {
    private List<Field> fields;
    private List<IndexField> indexes;

    private Schema(Builder builder) {
        this.fields = builder.fields;
        this.indexes = builder.indexes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Field> fields;
        private List<IndexField> indexes;

        private Builder() {
            this.fields = new ArrayList<>();
            this.indexes = new ArrayList<>();
        }

        public Builder addField(Field field) {
            this.fields.add(field);
            return this;
        }

        public Builder addIndex(IndexField index) {
            this.indexes.add(index);
            return this;
        }

        public Schema build() {
            return new Schema(this);
        }
    }
}