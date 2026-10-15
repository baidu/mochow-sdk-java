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
import com.baidu.mochow.model.entity.DistanceRange;
import com.baidu.mochow.model.entity.GeneralParams;
import com.baidu.mochow.model.entity.Vector;
import com.baidu.mochow.model.entity.VectorSearchConfig;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.fasterxml.jackson.annotation.JsonValue;

public class VectorBatchSearchRequest implements VectorSearchRequestInterface {
    private VectorBatchSearchRequest(VectorSearchFields fields) {
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
        return "batchSearch";
    }

    @Override
    public int getLimit() {
        return fields.limit;    
    }

    public static Builder builder(String vectorField, List<Vector> vectors) {
        return new Builder(vectorField, vectors);
    }

    public static Builder textBuilder(String textField, List<String> texts) {
        return new Builder(textField, texts, false);
    }

    public static class Builder {
        Builder(String vectorField, List<Vector> vectors) {
            this.fields = new VectorSearchFields();
            this.fields.vectorField = vectorField;
            this.fields.vectors = vectors;
        }

        Builder(String textField, List<String> texts, boolean unused) {
            this.fields = new VectorSearchFields();
            this.fields.textField = textField;
            this.fields.texts = texts;
        }

        public VectorBatchSearchRequest build() {
            return new VectorBatchSearchRequest(this.fields);
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

        public Builder distanceRange(DistanceRange distanceRange) {
            this.fields.distanceFar = distanceRange.getMax();
            this.fields.hasDistanceFar = true;
            this.fields.distanceNear = distanceRange.getMin();
            this.fields.hasDistanceNear = true;
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

        public Builder vectorField(String vectorField) {
            this.fields.vectorField = vectorField;
            return this;
        }

        public Builder textField(String textField) {
            this.fields.textField = textField;
            return this;
        }

        public Builder texts(List<String> texts) {
            this.fields.texts = texts;
            return this;
        }

        private VectorSearchFields fields;
    }
    private VectorSearchFields fields;
}
