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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FLATSearchParams extends SearchParams {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float distanceNear;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private float distanceFar;

    private FLATSearchParams(Builder builder) {
        setLimit(builder.limit);
        this.distanceNear = builder.distanceNear;
        this.distanceFar = builder.distanceFar;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int limit;
        private float distanceNear;
        private float distanceFar;

        public Builder() {
            this.limit = 50;
        }

        public Builder limit(int limit) {
            this.limit = limit;
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

        public FLATSearchParams build() {
            return new FLATSearchParams(this);
        }
    }
}