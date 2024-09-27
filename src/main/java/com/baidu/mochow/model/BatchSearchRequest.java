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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.baidu.mochow.model.entity.BatchANNSearchParams;
import com.baidu.mochow.model.entity.GeneralParams;
import com.baidu.mochow.model.enums.ReadConsistency;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BatchSearchRequest extends AbstractMochowRequest {
    private String database;
    private String table;
    private BatchANNSearchParams anns;
    private GeneralParams partitionKey;
    boolean retrieveVector;
    List<String> projections;
    ReadConsistency readConsistency;

    public BatchSearchRequest(Builder builder) {
        this.database = builder.database;
        this.table = builder.table;
        this.anns = builder.anns;
        this.partitionKey = builder.partitionKey;
        this.retrieveVector = builder.retrieveVector;
        this.projections = builder.projections;
        this.readConsistency = builder.readConsistency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String database;
        private String table;
        private BatchANNSearchParams anns;
        private GeneralParams partitionKey;
        boolean retrieveVector;
        List<String> projections;
        ReadConsistency readConsistency;

        public Builder() {
            this.projections = new ArrayList<String>();
            this.retrieveVector = false;
            this.readConsistency = ReadConsistency.EVENTUAL;
        }

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder anns(BatchANNSearchParams anns) {
            this.anns = anns;
            return this;
        }

        public Builder retrieveVector(Boolean retrieveVector) {
            this.retrieveVector = retrieveVector;
            return this;
        }

        public Builder readConsistency(ReadConsistency readConsistency) {
            this.readConsistency = readConsistency;
            return this;
        }

        public Builder projections(List<String> projections) {
            this.projections = projections;
            return this;
        }

        public Builder addPartitionKey(String key, Object value) {
            if (this.partitionKey == null) {
                this.partitionKey = new GeneralParams();
            }
            this.partitionKey.add(key, value);
            return this;
        }

        public Builder partitionKey(GeneralParams partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        public BatchSearchRequest build() {
            return new BatchSearchRequest(this);
        }
    }
}