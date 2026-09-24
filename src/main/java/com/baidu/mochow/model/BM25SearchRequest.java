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
import com.baidu.mochow.model.entity.HighlightParams;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.fasterxml.jackson.annotation.JsonValue;

public class BM25SearchRequest implements BM25SearchRequestInterface, IterableSearchRequestInterface {
    private BM25SearchRequest(BM25SearchFields fields) {
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

    public static Builder builder(String indexName, String searchText) {
        return new Builder(indexName, searchText);
    }

    public static class Builder {
        Builder(String indexName, String searchText) {
            this.fields = new BM25SearchFields();
            this.fields.indexName = indexName;
            this.fields.searchText = searchText;
        }

        public BM25SearchRequest build() {
            return new BM25SearchRequest(this.fields);
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

        public Builder synonyms(List<List<String>> synonyms) {
            this.fields.synonyms = synonyms;
            return this;
        }

        public Builder highlight(HighlightParams highlight) {
            this.fields.highlight = highlight;
            return this;
        }

        public Builder decay(List<com.baidu.mochow.model.entity.DecayFunction> decay) {
            this.fields.decay = decay;
            return this;
        }

        public Builder iteratedIds(String iteratedIds) {
            this.fields.iteratedIds = iteratedIds;
            this.fields.hasIteratedIds = true;
            return this;
        }

        private BM25SearchFields fields;
    }
    private BM25SearchFields fields;
}
