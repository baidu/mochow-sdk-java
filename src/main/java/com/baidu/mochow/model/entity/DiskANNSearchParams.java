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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DiskANNSearchParams extends SearchParams {
    private int w;
    private int searchL;

    private DiskANNSearchParams(Builder builder) {
        setLimit(builder.limit);
        this.w = builder.w;
        this.searchL = builder.searchL;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int limit;
        private int w;
        private int searchL;

        public Builder() {
            this.limit = 10;
            this.w = 1;
            this.searchL = 100;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder w(int w) {
            this.w = w;
            return this;
        }

        public Builder searchL(int searchL) {
            this.searchL = searchL;
            return this;
        }

        public DiskANNSearchParams build() {
            return new DiskANNSearchParams(this);
        }
    }
}

