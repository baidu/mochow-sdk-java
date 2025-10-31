/*
 * Copyright 2025 Baidu, Inc.
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
import lombok.Getter;
import lombok.Setter;

/**
 * HNSWSQ (HNSW with Scalar Quantization) search parameters.
 */
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HNSWSQSearchParams extends SearchParams {
    private int ef;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float distanceFar;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float distanceNear;

    public HNSWSQSearchParams(Builder builder) {
        setLimit(builder.limit);
        this.ef = builder.ef;
        this.distanceFar = builder.distanceFar;
        this.distanceNear = builder.distanceNear;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int ef;
        private Float distanceFar;
        private Float distanceNear;
        private int limit;

        public Builder() {
            this.limit = 50;
            this.distanceNear = null;
            this.distanceFar = null;
        }

        public Builder ef(int ef) {
            this.ef = ef;
            return this;
        }

        public Builder distanceNear(float distanceNear) {
            this.distanceNear = distanceNear;
            return this;
        }

        public Builder distanceFar(float distanceFar) {
            this.distanceFar = distanceFar;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public HNSWSQSearchParams build() {
            return new HNSWSQSearchParams(this);
        }
    }

    @Override
    public String toString() {
        return "HNSWSQSearchParams{" +
                "ef=" + ef +
                ", distanceFar=" + distanceFar +
                ", distanceNear=" + distanceNear +
                ", limit=" + getLimit() +
                '}';
    }
}

