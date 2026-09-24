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

package com.baidu.mochow.model;

import java.util.Map;

public class HybridSearchFields extends SearchCommonFields {
    VectorSearchRequestInterface vectorRequest;
    BM25SearchRequestInterface bm25Request;
    float vectorWeight;
    float bm25Weight;
    public String iteratedIds = "";
    public boolean hasIteratedIds;

    public void fillSearchFields(Map<String, Object> fields) {
        if (vectorRequest != null) {
            vectorRequest.toMap().forEach(fields::put);
        }

        if (bm25Request != null) {
            bm25Request.toMap().forEach(fields::put);
        }

        for (Map.Entry<String, Object> entry : toMap().entrySet()) {
            fields.put(entry.getKey(), entry.getValue());
        }

        if (hasIteratedIds) {
            fields.put("iteratedIds", iteratedIds);
        } else {
            fields.remove("iteratedIds");
        }

        if (fields.containsKey("anns")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> anns = (Map<String, Object>) fields.get("anns");
            anns.put("weight", vectorWeight);
        }

        if (fields.containsKey("BM25SearchParams")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> bm25 = (Map<String, Object>) fields.get("BM25SearchParams");
            bm25.put("weight", bm25Weight);
        }
    }
} 