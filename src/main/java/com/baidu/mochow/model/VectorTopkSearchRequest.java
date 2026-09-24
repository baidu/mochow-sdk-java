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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mochow.model.entity.AdvancedOptions;
import com.baidu.mochow.model.entity.GeneralParams;
import com.baidu.mochow.model.entity.Vector;
import com.baidu.mochow.model.entity.VectorSearchConfig;
import com.baidu.mochow.model.entity.DecayFunction;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.fasterxml.jackson.annotation.JsonValue;

public class VectorTopkSearchRequest implements SingleVectorSearchRequestInterface,
        IterableSearchRequestInterface {
    private VectorTopkSearchRequest(VectorSearchFields fields) {
        this.fields = fields;
    }

    @JsonValue
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        fields.fillSearchFields(map);
        return map;
    }

    @Override
    public String requestType() {
        return "search";
    }

    @Override
    public int getLimit() {
        return fields.limit;
    }

    public String getIteratedIds() {
        return fields.iteratedIds;
    }

    public void setIteratedIds(String iteratedIds) {
        fields.iteratedIds = iteratedIds;
        fields.hasIteratedIds = true;
    }

    public static Builder builder(String vectorField, Vector vector, int limit) {
        return new Builder(vectorField, vector, limit);
    }

    public static class Builder{
        Builder(String vectorField, Vector vector, int limit) {
            this.fields = new VectorSearchFields();
            this.fields.vectorField = vectorField;
            this.fields.vector = vector;
            this.fields.limit = limit;
            this.fields.hasLimit = true;
        }

        public VectorTopkSearchRequest build() {
            return new VectorTopkSearchRequest(this.fields);
        }

        public Builder partitionKey(GeneralParams partitionKey) {
            this.fields.partitionKey = partitionKey;
            return this;
        }

        public Builder readConsistency(ReadConsistency readConsistency) {
            this.fields.readConsistency = readConsistency;
            return this;
        }

        public Builder projections(List<String> projections) {
            this.fields.projections = projections;
            return this;
        }

        public Builder filter(String filter) {
            this.fields.filter = filter;
            return this;
        }

        public Builder config(VectorSearchConfig config) {
            this.fields.config = config;
            return this;
        }

        public Builder advancedOptions(AdvancedOptions advancedOptions) {
            this.fields.advancedOptions = advancedOptions;
            return this;
        }

        public Builder iteratedIds(String iteratedIds) {
            this.fields.iteratedIds = iteratedIds;
            this.fields.hasIteratedIds = true;
            return this;
        }

        public Builder decay(List<DecayFunction> decay) {
            this.fields.decay = decay;
            return this;
        }

        private VectorSearchFields fields;
    }

    private VectorSearchFields fields;
}
