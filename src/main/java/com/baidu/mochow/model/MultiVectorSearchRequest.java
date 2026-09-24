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
import com.baidu.mochow.model.entity.FusionRankPolicy;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.fasterxml.jackson.annotation.JsonValue;

public class MultiVectorSearchRequest implements VectorSearchRequestInterface,
        IterableSearchRequestInterface {
    private MultiVectorSearchRequest(MultiVectorSearchFields fields) {
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
        return "multiVectorSearch";
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

    public static Builder builder(List<SingleVectorSearchRequestInterface> requests) {
        return new Builder(requests);
    }

    public static class Builder{
        Builder(List<SingleVectorSearchRequestInterface> requests) {
            this.fields = new MultiVectorSearchFields();
            this.fields.requests = requests;
        }

        public MultiVectorSearchRequest build() {
            return new MultiVectorSearchRequest(this.fields);
        }

        public Builder rankPolicy(FusionRankPolicy policy) {
            this.fields.ranking = policy;
            return this;
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

        public Builder limit(int limit) {
            this.fields.hasLimit = true;
            this.fields.limit = limit;
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

        private MultiVectorSearchFields fields;
    }

    private MultiVectorSearchFields fields;
}
