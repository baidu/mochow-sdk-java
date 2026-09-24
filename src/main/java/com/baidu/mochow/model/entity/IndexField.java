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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.baidu.mochow.model.enums.IndexState;
import com.baidu.mochow.model.enums.IndexType;
import com.baidu.mochow.model.enums.MetricType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IndexField {
    private String indexName;
    private String field;

    private IndexType indexType;
    private IndexState state;

    // vector index
    private MetricType metricType;
    @JsonDeserialize(using=IndexParamsDeserializer.class)
    private IndexParams params;
    private List<Integer> truncationDimension;

    // auto build
    private boolean autoBuild = false;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private AutoBuildPolicy autoBuildPolicy = null;

    @JsonIgnore
    public boolean isVectorIndex() {
        return IndexType.HNSW.equals(this.indexType) || IndexType.HNSWPQ.equals(this.indexType)
                || IndexType.HNSWSQ.equals(this.indexType)
                || IndexType.HNSWRABITQ.equals(this.indexType)
                || IndexType.PUCK.equals(this.indexType)
                || IndexType.DISKANN.equals(this.indexType)
                || IndexType.DISKANNRABITQ.equals(this.indexType)
                || IndexType.IVF.equals(this.indexType)
                || IndexType.IVFPQ.equals(this.indexType)
                || IndexType.IVFSQ.equals(this.indexType)
                || IndexType.SPARSE_OPTIMIZED_FLAT.equals(this.indexType);
    }
}
