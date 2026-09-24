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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.baidu.mochow.model.entity.HighlightParams;

public class BM25SearchFields extends SearchCommonFields {
    public String indexName;
    public String searchText;
    public List<List<String>> synonyms;
    public HighlightParams highlight;
    public String iteratedIds = "";
    public boolean hasIteratedIds;

    public void fillSearchFields(Map<String, Object> fields) {
        for (Map.Entry<String, Object> entry : toMap().entrySet()) {
            fields.put(entry.getKey(), entry.getValue());
        }

        if (hasIteratedIds) {
            fields.put("iteratedIds", iteratedIds);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("indexName", indexName);
        params.put("searchText", searchText);
        if (synonyms != null) {
            params.put("synonyms", synonyms);
        }
        if (highlight != null) {
            params.put("highlight", highlight);
        }

        fields.put("BM25SearchParams", params);
    }
} 
