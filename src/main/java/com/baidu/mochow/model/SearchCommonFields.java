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
import java.util.List;
import java.util.Map;

import com.baidu.mochow.model.entity.GeneralParams;
import com.baidu.mochow.model.entity.DecayFunction;
import com.baidu.mochow.model.enums.ReadConsistency;

public class SearchCommonFields {
    public GeneralParams partitionKey;
    public List<String> projections;
    public ReadConsistency readConsistency;
    public boolean hasLimit;
    public int limit;
    public String filter;
    public List<DecayFunction> decay;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (partitionKey != null) {
            map.put("partitionKey", partitionKey);
        }
        if (projections != null) {
            map.put("projections", projections);
        }
        if (readConsistency != null) {
            map.put("readConsistency", readConsistency);
        }
        if (hasLimit) {
            map.put("limit", limit);
        }
        if (filter != null) {
            map.put("filter", filter);
        }
        if (decay != null) {
            map.put("decay", decay);
        }
        return map;
    }
} 
