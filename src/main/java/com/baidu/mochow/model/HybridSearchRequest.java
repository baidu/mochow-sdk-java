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

import com.baidu.mochow.model.entity.GeneralParams;
import com.baidu.mochow.model.entity.DecayFunction;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.fasterxml.jackson.annotation.JsonValue;

public class HybridSearchRequest implements HybridSearchRequestInterface, IterableSearchRequestInterface {
    private HybridSearchRequest(HybridSearchFields fields) {
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

    @Override
    public void setIteratedIds(String iteratedIds) {
        fields.iteratedIds = iteratedIds;
        fields.hasIteratedIds = true;
    }

    public static Builder builder(VectorSearchRequestInterface vectorRequest,
                           BM25SearchRequestInterface bm25Request,
                           float vectorWeight,
                           float bm25Weight) {
        return new Builder(vectorRequest, bm25Request, vectorWeight, bm25Weight);
    }

    public static class Builder {
        Builder(VectorSearchRequestInterface vectorRequest,
                       BM25SearchRequestInterface bm25Request,
                       float vectorWeight,
                       float bm25Weight) {
            this.fields = new HybridSearchFields();
            this.fields.vectorRequest = vectorRequest;
            this.fields.bm25Request = bm25Request;
            this.fields.vectorWeight = vectorWeight;
            this.fields.bm25Weight = bm25Weight;
        }

        public HybridSearchRequest build() {
            return new HybridSearchRequest(this.fields);
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

        public Builder limit(int count) {
            this.fields.limit = count;
            this.fields.hasLimit = true;
            return this;
        }

        public Builder filter(String filter) {
            this.fields.filter = filter;
            return this;
        }

        public Builder decay(List<DecayFunction> decay) {
            this.fields.decay = decay;
            return this;
        }

        public Builder iteratedIds(String iteratedIds) {
            this.fields.iteratedIds = iteratedIds;
            this.fields.hasIteratedIds = true;
            return this;
        }

        private HybridSearchFields fields;
    }
    private HybridSearchFields fields;
}
